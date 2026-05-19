package com.wye.agent;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.wye.entity.AiConfig;
import com.wye.mapper.AiConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PropertyAgent {

    private static final Logger log = LoggerFactory.getLogger(PropertyAgent.class);
    private static final int MAX_TOOL_ROUNDS = 5;

    @Value("${ai.url}")
    private String defaultAiUrl;

    @Value("${ai.model}")
    private String defaultAiModel;

    @Autowired
    private AiConfigMapper aiConfigMapper;

    @Autowired
    private PropertyAgentTools propertyAgentTools;

    public Map<String, Object> chat(Long userId, String message, List<Map<String, Object>> history) {
        AiConfig aiConfig = aiConfigMapper.selectEnabled();
        String apiUrl = aiConfig != null && aiConfig.getApiUrl() != null ? aiConfig.getApiUrl() : defaultAiUrl;
        String modelName = aiConfig != null && aiConfig.getModelName() != null ? aiConfig.getModelName() : defaultAiModel;
        String apiKey = aiConfig != null ? aiConfig.getApiKey() : null;

        List<Map<String, Object>> messages = buildMessages(userId, message, history);
        List<Map<String, Object>> tools = PropertyAgentTools.getToolList();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("messages", messages);
        requestBody.put("tools", tools);
        requestBody.put("tool_choice", "auto");

        List<Map<String, Object>> toolCallsLog = new ArrayList<>();
        String finalContent = null;

        for (int round = 0; round < MAX_TOOL_ROUNDS; round++) {
            log.info("Agent第{}轮调用, userId={}", round + 1, userId);

            requestBody.put("messages", messages);

            String responseBody = callLlmApi(apiUrl, apiKey, requestBody);
            if (responseBody == null) {
                return buildErrorResponse("AI服务暂时不可用，请稍后再试");
            }

            Map<String, Object> responseMap = JSONUtil.toBean(responseBody, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            if (choices == null || choices.isEmpty()) {
                return buildErrorResponse("AI返回结果异常");
            }

            Map<String, Object> assistantMessage = (Map<String, Object>) choices.get(0).get("message");
            messages.add(assistantMessage);

            Object toolCallsObj = assistantMessage.get("tool_calls");
            if (toolCallsObj == null || toolCallsObj instanceof cn.hutool.json.JSONNull || !(toolCallsObj instanceof List)) {
                Object contentObj = assistantMessage.get("content");
                finalContent = contentObj != null && !(contentObj instanceof cn.hutool.json.JSONNull) ? contentObj.toString() : null;
                break;
            }

            List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) toolCallsObj;
            for (Map<String, Object> toolCall : toolCalls) {
                String toolCallId = (String) toolCall.get("id");
                Map<String, Object> function = (Map<String, Object>) toolCall.get("function");
                String functionName = (String) function.get("name");
                String argumentsJson = (String) function.get("arguments");

                log.info("Agent调用工具: {}({})", functionName, argumentsJson);

                String toolResult;
                try {
                    Map<String, Object> arguments = JSONUtil.toBean(argumentsJson, Map.class);
                    toolResult = propertyAgentTools.executeTool(functionName, arguments);
                } catch (Exception e) {
                    log.error("工具执行异常: {}", functionName, e);
                    toolResult = "{\"error\":\"工具执行失败: " + e.getMessage() + "\"}";
                }

                log.info("工具返回结果: {}", toolResult);

                Map<String, Object> toolResultMessage = new HashMap<>();
                toolResultMessage.put("role", "tool");
                toolResultMessage.put("tool_call_id", toolCallId);
                toolResultMessage.put("content", toolResult);
                messages.add(toolResultMessage);

                Map<String, Object> callLog = new HashMap<>();
                callLog.put("tool", functionName);
                callLog.put("arguments", argumentsJson);
                callLog.put("result", toolResult);
                toolCallsLog.add(callLog);
            }
        }

        if (finalContent == null) {
            finalContent = "抱歉，处理您的请求时遇到了问题，请稍后再试。";
        }

        finalContent = stripThinking(finalContent);

        Map<String, Object> result = new HashMap<>();
        result.put("reply", finalContent);
        result.put("tool_calls", toolCallsLog);
        result.put("rounds", Math.min(toolCallsLog.size() + 1, MAX_TOOL_ROUNDS));
        return result;
    }

    private List<Map<String, Object>> buildMessages(Long userId, String message, List<Map<String, Object>> history) {
        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content",
            "你是智慧社区物业管理系统的AI助手，你不仅可以回答问题，还可以执行操作。\n" +
            "当前用户ID为: " + userId + "\n\n" +
            "你可以使用以下工具来帮助用户：\n" +
            "【业主常用工具】\n" +
            "1. query_unpaid_fees - 查询业主欠费信息\n" +
            "2. query_fee_settings - 查询物业费、水费、电费、燃气费的收费标准\n" +
            "3. create_repair - 创建报修工单\n" +
            "4. query_repair_status - 查询报修进度\n" +
            "5. query_notices - 查询小区通知\n" +
            "6. query_owner_info - 查询业主信息\n" +
            "7. evaluate_repair - 对已完成的维修服务进行评价打分\n" +
            "8. query_activities - 查询社区活动信息\n" +
            "9. submit_feedback - 提交意见反馈或投诉建议\n" +
            "10. create_forum_post - 在社区论坛发帖，不指定分区时自动判断\n" +
            "11. search_forum_posts - 模糊搜索论坛帖子\n\n" +
            "12. signup_activity - 报名参加社区活动\n" +
            "13. query_notifications - 查询用户的通知消息\n" +
            "14. mark_notification_read - 标记通知为已读\n\n" +
            "【管理员专用工具】\n" +
            "15. dispatch_repair - 为报修工单派单，指定维修员处理\n" +
            "16. query_available_workers - 查询当前可用的维修员列表\n" +
            "17. query_dashboard_stats - 查询系统仪表盘统计数据\n" +
            "18. urge_payment - 一键催缴，对所有未缴费业主发送催缴提醒\n" +
            "19. publish_notice - 一键发布小区通知公告，可设置普通或紧急类型\n\n" +
            "工作原则：\n" +
            "- 当用户反映设施故障、漏水、停电等问题时，主动使用create_repair创建报修单\n" +
            "- 当用户询问费用相关问题时，调用query_unpaid_fees查询欠费，或调用query_fee_settings查询收费标准\n" +
            "- 当用户想评价维修服务时，先确认工单已完成，再调用evaluate_repair；如果用户没有指定工单ID，不填repair_id即可自动评价最近已完成的工单\n" +
            "- 当业主想在论坛发帖时，调用create_forum_post；如果业主没有指定分区，不填category_name，系统会根据内容自动判断\n" +
            "- 当用户想搜索论坛帖子时，调用search_forum_posts进行模糊搜索\n" +
            "- 当管理员需要派单时，先查询可用维修员，再调用dispatch_repair\n" +
            "- 当管理员询问系统概况时，调用query_dashboard_stats获取统计数据\n" +
            "- 当管理员需要催缴时，调用urge_payment一键催缴\n" +
            "- 当管理员需要发布通知时，调用publish_notice，标题和内容由管理员提供，紧急通知设置type=1\n" +
            "- 当用户想报名活动时，调用signup_activity\n" +
            "- 当用户想查看通知时，调用query_notifications\n" +
            "- 回答要简洁友好，执行操作后告知用户结果\n" +
            "- 如果用户只是闲聊，正常回答即可，无需调用工具"
        );
        messages.add(systemMsg);

        if (history != null && !history.isEmpty()) {
            int start = Math.max(0, history.size() - 10);
            for (int i = start; i < history.size(); i++) {
                messages.add(history.get(i));
            }
        }

        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", message);
        messages.add(userMsg);

        return messages;
    }

    private String callLlmApi(String apiUrl, String apiKey, Map<String, Object> requestBody) {
        try {
            HttpRequest request = HttpRequest.post(apiUrl)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(requestBody))
                    .timeout(120000)
                    .setConnectionTimeout(60000);

            if (apiKey != null && !apiKey.isEmpty()) {
                request.header("Authorization", "Bearer " + apiKey);
            }

            HttpResponse response = request.execute();

            if (response.getStatus() == 200) {
                return response.body();
            } else {
                log.error("LLM API调用失败, status={}, body={}", response.getStatus(), response.body());
                return null;
            }
        } catch (Exception e) {
            log.error("LLM API调用异常", e);
            return null;
        }
    }

    private Map<String, Object> buildErrorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("reply", message);
        result.put("tool_calls", new ArrayList<>());
        result.put("rounds", 0);
        return result;
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
