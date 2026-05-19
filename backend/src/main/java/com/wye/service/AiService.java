package com.wye.service;

import java.io.File;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wye.entity.AiConfig;
import com.wye.entity.BusActivity;
import com.wye.entity.BusFee;
import com.wye.entity.BusFeeSettings;
import com.wye.entity.BusNotice;
import com.wye.mapper.AiConfigMapper;
import com.wye.mapper.BusActivityMapper;
import com.wye.mapper.BusFeeMapper;
import com.wye.mapper.BusNoticeMapper;
import com.wye.mapper.FeeSettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {
   
            @Value("${ai.url}")
    private String defaultAiUrl;
   
            @Value("${ai.model}")
    private String defaultAiModel;
   
            @Value("${file.upload.url-prefix}")
    private String uploadUrlPrefix;
   
            @Value("${file.upload.path}")
    private String uploadPath;
   
            @Value("${server.url:http://localhost:8080}")
    private String serverUrl;
   
            @Autowired
    private AiConfigMapper aiConfigMapper;
   
            @Autowired
    private BusFeeMapper busFeeMapper;
   
            @Autowired
    private BusNoticeMapper busNoticeMapper;
   
            @Autowired
    private BusActivityMapper busActivityMapper;
   
            @Autowired
    private FeeSettingsMapper feeSettingsMapper;
   
            /**
      * 生成AI回复
      */
            public String generateReply(Long userId, String userQuestion) {
        return generateReply(userId, userQuestion, null);
    }

    /**
      * 生成AI回复（支持图片）
      */
            public String generateReply(Long userId, String userQuestion, String imageUrl) {
        StringBuilder context = new StringBuilder();
        context.append("【用户相关信息】\n");
        context.append("用户ID: ").append(userId).append("\n");
       
        // 1. 检索用户欠费信息
        List<BusFee> unpaidFees = busFeeMapper.selectUnpaid(userId);
       
        System.out.println("查询用户ID: " + userId + " 的欠费记录，结果数量: " + unpaidFees.size());
       
        if (!unpaidFees.isEmpty()) {
            BigDecimal totalUnpaid = BigDecimal.ZERO;
            context.append("【欠费情况】:\n");
            for (BusFee fee : unpaidFees) {
                System.out.println("欠费记录: owner_id=" + fee.getOwnerId() + ", type=" + fee.getType() + ", month=" + fee.getMonth() + ", amount=" + fee.getAmount() + ", status=" + fee.getStatus());
                BigDecimal amount = fee.getAmount() != null ? fee.getAmount() : BigDecimal.ZERO;
                totalUnpaid = totalUnpaid.add(amount);
                context.append("- ").append(fee.getMonth()).append(" ").append(fee.getType()).append("费: ").append(amount).append("元\n");
            }
            context.append("合计欠费: ").append(totalUnpaid).append("元\n");
        } else {
            context.append("【欠费情况】: 无欠费\n");
        }
       
        // 2. 检索物业费收费标准
        List<BusFeeSettings> feeSettingsList = feeSettingsMapper.selectList(null);
        if (!feeSettingsList.isEmpty()) {
            BusFeeSettings feeSettings = feeSettingsList.get(0);
            context.append("\n【物业费收费标准】:\n");
            if (feeSettings.getPropertyFee() != null) {
                context.append("- 物业费: ").append(feeSettings.getPropertyFee()).append("元/平方米\n");
            }
            if (feeSettings.getWaterFee() != null) {
                context.append("- 水费: ").append(feeSettings.getWaterFee()).append("元/吨\n");
            }
            if (feeSettings.getElectricityFee() != null) {
                context.append("- 电费: ").append(feeSettings.getElectricityFee()).append("元/度\n");
            }
            if (feeSettings.getGasFee() != null) {
                context.append("- 燃气费: ").append(feeSettings.getGasFee()).append("元/立方\n");
            }
        }
       
        // 3. 检索最新小区通知
        List<BusNotice> notices = busNoticeMapper.selectList(
                        new QueryWrapper<BusNotice>()
                .orderByDesc("publish_time")
                .last("LIMIT 3")
        );
        if (!notices.isEmpty()) {
            context.append("\n【最新小区通知】:\n");
            for (BusNotice notice : notices) {
                context.append("- ").append(notice.getTitle());
                if (notice.getType() != null && notice.getType() == 1) {
                    context.append(" (紧急)");
                }
                context.append("\n");
            }
        }
       
        // 4. 检索最新小区活动
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<BusActivity> activities = busActivityMapper.selectList(
                        new QueryWrapper<BusActivity>()
                .orderByDesc("start_time")
                .last("LIMIT 3")
        );
        if (!activities.isEmpty()) {
            context.append("\n【最新小区活动】:\n");
            for (BusActivity activity : activities) {
                context.append("- ").append(activity.getTitle());
                if (activity.getStartTime() != null) {
                    context.append(" (时间: ").append(dateFormat.format(activity.getStartTime())).append(")");
                }
                if (activity.getLocation() != null) {
                    context.append(" (地点: ").append(activity.getLocation()).append(")");
                }
                context.append("\n");
            }
        }
       
        // 5. 获取AI配置
        AiConfig aiConfig = aiConfigMapper.selectEnabled();
        String apiUrl = aiConfig != null && aiConfig.getApiUrl() != null ? aiConfig.getApiUrl() : defaultAiUrl;
        String modelName = aiConfig != null && aiConfig.getModelName() != null ? aiConfig.getModelName() : defaultAiModel;
        String apiKey = aiConfig != null ? aiConfig.getApiKey() : null;
       
        System.out.println("使用AI配置: provider=" + (aiConfig != null ? aiConfig.getProvider() : "default") + ", url=" + apiUrl + ", model=" + modelName);
       
        // 6. 构造AI请求
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
       
        String systemPrompt = "你是一个友好专业的智慧社区客服。\n\n" +
            "【用户相关信息】中包含了该用户的欠费情况、物业费收费标准、小区通知和活动等数据。\n\n" +
            "回答要求：\n" +
            "1. 如果用户询问欠费、收费标准、通知、活动等问题，请优先使用【用户相关信息】中的数据来回答\n" +
            "2. 如果用户询问的是其他通用问题（如数学计算、常识问答等），请正常回答，不要局限于【用户相关信息】\n" +
            "3. 回答要简洁、友好、准确\n" +
            "4. 保持客服的专业形象，语气亲切自然";
       
        System.out.println("=== AI Context ===");
        System.out.println(systemPrompt + "\n\n" + context.toString());
        System.out.println("==================");
       
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt + "\n\n" + context.toString());
       
        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
       
        if (imageUrl != null && !imageUrl.isEmpty()) {
            List<Map<String, Object>> contentList = new ArrayList<>();
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", userQuestion);
            contentList.add(textContent);
           
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            Map<String, String> imageUrlMap = new HashMap<>();
           
            String fullImageUrl;
            String base64Image = null;
           
            if (imageUrl.startsWith("http")) {
                fullImageUrl = imageUrl;
                imageUrlMap.put("url", fullImageUrl);
            } else {
                fullImageUrl = serverUrl + imageUrl;
                try {
                    String imagePath = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
                    if (imagePath.startsWith("upload/")) {
                        imagePath = imagePath.substring(7);
                    }
                    String fullPath = new File(uploadPath).getAbsoluteFile() + "/" + imagePath;
                    byte[] imageBytes = Files.readAllBytes(Paths.get(fullPath));
                    base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
                    imageUrlMap.put("url", base64Image);
                } catch (Exception e) {
                    System.out.println("读取图片失败: " + e.getMessage());
                    imageUrlMap.put("url", fullImageUrl);
                }
            }
           
            imageContent.put("image_url", imageUrlMap);
            contentList.add(imageContent);
           
            userMsg.put("content", contentList);
        } else {
            userMsg.put("content", userQuestion);
        }
       
        requestBody.put("messages", new Object[]{systemMsg, userMsg});
       
        // 7. 调用AI接口
        try {
            System.out.println("准备调用AI接口:");
            System.out.println("  URL: " + apiUrl);
            System.out.println("  Model: " + modelName);
            System.out.println("  API Key: " + (apiKey != null && !apiKey.isEmpty() ? "已设置" : "未设置"));
            System.out.println("  Request Body: " + JSONUtil.toJsonStr(requestBody));
           
            HttpRequest request = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(requestBody))
                .timeout(60000)
                .setConnectionTimeout(60000);
           
            if (apiKey != null && !apiKey.isEmpty()) {
                request.header("Authorization", "Bearer " + apiKey);
            }
           
            HttpResponse response = request.execute();
           
            System.out.println("AI接口响应状态: " + response.getStatus());
            System.out.println("AI接口响应内容: " + response.body());
           
            if (response.getStatus() == 200) {
                String responseBody = response.body();
                Map<String, Object> responseMap = JSONUtil.toBean(responseBody, Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    return stripThinking(content);
                }
            } else {
                System.out.println("AI接口调用失败，状态码: " + response.getStatus() + ", 响应: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("AI接口调用异常: " + e.getMessage());
            e.printStackTrace();
        }
       
        // 8. AI调用失败时的备用回复
        return generateFallbackReply(userQuestion, unpaidFees, feeSettingsList, notices, activities);
    }
   
            /**
      * 当AI接口调用失败时的备用回复
      */
            private String generateFallbackReply(String question, List<BusFee> unpaidFees,
                                         List<BusFeeSettings> feeSettingsList,
                                         List<BusNotice> notices,
                                         List<BusActivity> activities) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        question = question.toLowerCase();
       
        if (question.contains("缴费") || question.contains("欠费") || question.contains("账单")) {
            if (!unpaidFees.isEmpty()) {
                BigDecimal totalUnpaid = BigDecimal.ZERO;
                StringBuilder reply = new StringBuilder("您有待缴费用：\n");
                for (BusFee fee : unpaidFees) {
                    BigDecimal amount = fee.getAmount() != null ? fee.getAmount() : BigDecimal.ZERO;
                    totalUnpaid = totalUnpaid.add(amount);
                    reply.append("- ").append(fee.getMonth()).append(" ").append(fee.getType()).append("费: ").append(amount).append("元\n");
                }
                reply.append("合计: ").append(totalUnpaid).append("元");
                return reply.toString();
            } else {
                return "您目前没有未缴费用，感谢您的关注！";
            }
        } else if (question.contains("收费") || question.contains("标准") || question.contains("价格")) {
            if (!feeSettingsList.isEmpty()) {
                BusFeeSettings feeSettings = feeSettingsList.get(0);
                StringBuilder reply = new StringBuilder("物业费收费标准：\n");
                if (feeSettings.getPropertyFee() != null) {
                    reply.append("- 物业费: ").append(feeSettings.getPropertyFee()).append("元/平方米\n");
                }
                if (feeSettings.getWaterFee() != null) {
                    reply.append("- 水费: ").append(feeSettings.getWaterFee()).append("元/吨\n");
                }
                if (feeSettings.getElectricityFee() != null) {
                    reply.append("- 电费: ").append(feeSettings.getElectricityFee()).append("元/度\n");
                }
                if (feeSettings.getGasFee() != null) {
                    reply.append("- 燃气费: ").append(feeSettings.getGasFee()).append("元/立方\n");
                }
                return reply.toString();
            } else {
                return "暂无收费标准信息，请联系物业咨询。";
            }
        } else if (question.contains("活动")) {
            if (!activities.isEmpty()) {
                StringBuilder reply = new StringBuilder("最新小区活动：\n");
                for (BusActivity activity : activities) {
                    reply.append("- ").append(activity.getTitle());
                    if (activity.getStartTime() != null) {
                        reply.append(" (时间: ").append(dateFormat.format(activity.getStartTime())).append(")");
                    }
                    if (activity.getLocation() != null) {
                        reply.append(" (地点: ").append(activity.getLocation()).append(")");
                    }
                    reply.append("\n");
                }
                return reply.toString();
            } else {
                return "目前没有最新的小区活动。";
            }
        } else if (question.contains("通知")) {
            if (!notices.isEmpty()) {
                StringBuilder reply = new StringBuilder("最新小区通知：\n");
                for (BusNotice notice : notices) {
                    reply.append("- ").append(notice.getTitle());
                    if (notice.getType() != null && notice.getType() == 1) {
                        reply.append(" (紧急)");
                    }
                    reply.append("\n");
                }
                return reply.toString();
            } else {
                return "目前没有最新的小区通知。";
            }
        } else {
            return "您好，我是智能客服，请问有什么可以帮助您？您可以询问缴费、收费标准、活动、通知等相关问题。";
        }
    }

    private String stripThinking(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        String result = content;

        while (true) {
            int start = result.indexOf("<think>");
            if (start < 0) break;
            int end = result.indexOf("</think>", start);
            if (end < 0) break;
            result = result.substring(0, start) + result.substring(end + "</think>".length());
        }

        while (true) {
            int start = result.indexOf("```thinking");
            if (start < 0) break;
            int end = result.indexOf("```", start + 3);
            if (end < 0) break;
            result = result.substring(0, start) + result.substring(end + 3);
        }

        return result.trim();
    }
}

