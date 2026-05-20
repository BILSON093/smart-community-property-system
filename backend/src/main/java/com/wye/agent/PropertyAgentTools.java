package com.wye.agent;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wye.entity.BusActivity;
import com.wye.entity.BusActivitySignup;
import com.wye.entity.BusEvaluation;
import com.wye.entity.BusFee;
import com.wye.entity.BusFeeSettings;
import com.wye.entity.BusFeedback;
import com.wye.entity.BusForum;
import com.wye.entity.BusForumCategory;
import com.wye.entity.BusNotice;
import com.wye.entity.BusRepair;
import com.wye.entity.SysNotification;
import com.wye.entity.SysOwner;
import com.wye.entity.SysRepairWorker;
import com.wye.entity.SysUser;
import com.wye.mapper.BusActivityMapper;
import com.wye.mapper.BusActivitySignupMapper;
import com.wye.mapper.BusEvaluationMapper;
import com.wye.mapper.BusFeeMapper;
import com.wye.mapper.BusFeedbackMapper;
import com.wye.mapper.BusForumCategoryMapper;
import com.wye.mapper.BusForumMapper;
import com.wye.mapper.BusNoticeMapper;
import com.wye.mapper.BusRepairMapper;
import com.wye.mapper.FeeSettingsMapper;
import com.wye.mapper.SysNotificationMapper;
import com.wye.mapper.SysOwnerMapper;
import com.wye.mapper.SysRepairWorkerMapper;
import com.wye.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONUtil;

@Component
public class PropertyAgentTools {

    @Autowired
    private BusFeeMapper busFeeMapper;

    @Autowired
    private BusRepairMapper busRepairMapper;

    @Autowired
    private BusNoticeMapper busNoticeMapper;

    @Autowired
    private BusActivityMapper busActivityMapper;

    @Autowired
    private BusEvaluationMapper busEvaluationMapper;

    @Autowired
    private BusFeedbackMapper busFeedbackMapper;

    @Autowired
    private FeeSettingsMapper feeSettingsMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysOwnerMapper sysOwnerMapper;

    @Autowired
    private SysRepairWorkerMapper sysRepairWorkerMapper;

    @Autowired
    private BusForumMapper busForumMapper;

    @Autowired
    private BusForumCategoryMapper busForumCategoryMapper;

    @Autowired
    private BusActivitySignupMapper busActivitySignupMapper;

    @Autowired
    private SysNotificationMapper sysNotificationMapper;

    public static List<Map<String, Object>> getToolList() {
        List<Map<String, Object>> tools = new ArrayList<>();

        tools.add(buildTool("query_unpaid_fees",
            "查询指定业主的欠费信息，包括水费、电费、物业费等未缴费用",
            buildParams(new String[][]{
                {"owner_id", "integer", "业主用户ID", "true"}
            })));

        tools.add(buildTool("query_fee_settings",
            "查询物业费、水费、电费、燃气费的收费标准",
            new HashMap<>()));

        tools.add(buildTool("create_repair",
            "为业主创建报修工单，当业主反映设施故障、漏水、停电等问题时使用",
            buildParams(new String[][]{
                {"owner_id", "integer", "业主用户ID", "true"},
                {"content", "string", "报修内容描述", "true"}
            })));

        tools.add(buildTool("query_repair_status",
            "查询业主的报修工单状态，包括待审核、已派单、维修中、已完成等",
            buildParams(new String[][]{
                {"owner_id", "integer", "业主用户ID", "true"}
            })));

        tools.add(buildTool("query_notices",
            "查询最新的小区通知公告",
            buildParams(new String[][]{
                {"limit", "integer", "返回通知数量，默认5", "false"}
            })));

        tools.add(buildTool("query_owner_info",
            "查询业主的详细信息，包括楼栋、单元、房间号等",
            buildParams(new String[][]{
                {"owner_id", "integer", "业主用户ID", "true"}
            })));

        tools.add(buildTool("dispatch_repair",
            "管理员为报修工单派单，指定维修员处理",
            buildParams(new String[][]{
                {"repair_id", "integer", "报修工单ID", "true"},
                {"worker_id", "integer", "维修员用户ID", "true"}
            })));

        tools.add(buildTool("query_available_workers",
            "查询当前可用的维修员列表，用于派单时选择维修员",
            new HashMap<>()));

        tools.add(buildTool("evaluate_repair",
            "业主对已完成的维修服务进行评价打分，不指定工单ID时自动评价最近一个已完成的工单",
            buildParams(new String[][]{
                {"owner_id", "integer", "业主用户ID", "true"},
                {"repair_id", "integer", "报修工单ID，不填则自动评价最近已完成的工单", "false"},
                {"score", "integer", "评分1-5分", "true"},
                {"comment", "string", "评价内容", "false"}
            })));

        tools.add(buildTool("query_activities",
            "查询最新的社区活动信息，包括活动时间、地点等",
            buildParams(new String[][]{
                {"limit", "integer", "返回活动数量，默认5", "false"}
            })));

        tools.add(buildTool("submit_feedback",
            "提交业主意见反馈或投诉建议",
            buildParams(new String[][]{
                {"user_id", "integer", "提交用户ID", "true"},
                {"user_name", "string", "提交用户姓名", "true"},
                {"content", "string", "反馈内容", "true"}
            })));

        tools.add(buildTool("query_dashboard_stats",
            "查询物业管理系统仪表盘统计数据，包括报修数、缴费率、业主数等概览信息",
            new HashMap<>()));

        tools.add(buildTool("urge_payment",
            "管理员一键催缴，对所有未缴费的业主发送催缴提醒",
            new HashMap<>()));

        tools.add(buildTool("publish_notice",
            "管理员一键发布小区通知公告，可设置普通或紧急类型",
            buildParams(new String[][]{
                {"title", "string", "通知标题", "true"},
                {"content", "string", "通知内容", "true"},
                {"type", "integer", "通知类型：0=普通(默认)，1=紧急", "false"}
            })));

        tools.add(buildTool("create_forum_post",
            "业主在社区论坛发帖，如果不指定分区则根据内容自动判断最合适的分区",
            buildParams(new String[][]{
                {"user_id", "integer", "发帖用户ID", "true"},
                {"title", "string", "帖子标题", "true"},
                {"content", "string", "帖子内容", "true"},
                {"category_name", "string", "分区名称，不填则自动判断", "false"}
            })));

        tools.add(buildTool("search_forum_posts",
            "模糊搜索论坛帖子，根据关键词搜索标题和内容",
            buildParams(new String[][]{
                {"keyword", "string", "搜索关键词", "true"},
                {"limit", "integer", "返回结果数量，默认5", "false"}
            })));

        tools.add(buildTool("signup_activity",
            "报名参加社区活动",
            buildParams(new String[][]{
                {"user_id", "integer", "用户ID", "true"},
                {"activity_id", "integer", "活动ID", "true"}
            })));

        tools.add(buildTool("query_notifications",
            "查询用户的通知消息列表",
            buildParams(new String[][]{
                {"user_id", "integer", "用户ID", "true"},
                {"limit", "integer", "返回数量，默认5", "false"}
            })));

        tools.add(buildTool("mark_notification_read",
            "标记通知为已读",
            buildParams(new String[][]{
                {"notification_id", "integer", "通知ID", "true"}
            })));

        tools.add(buildTool("cancel_activity_signup",
            "取消已报名的社区活动",
            buildParams(new String[][]{
                {"user_id", "integer", "用户ID", "true"},
                {"activity_id", "integer", "活动ID", "true"}
            })));

        tools.add(buildTool("query_my_activities",
            "查询用户已报名的社区活动列表",
            buildParams(new String[][]{
                {"user_id", "integer", "用户ID", "true"}
            })));

        tools.add(buildTool("query_fee_history",
            "查询业主的缴费历史记录，包括已缴和未缴的所有账单",
            buildParams(new String[][]{
                {"owner_id", "integer", "业主用户ID", "true"},
                {"status", "integer", "筛选状态：0=未缴，1=已缴，不填=全部", "false"}
            })));

        tools.add(buildTool("query_my_forum_posts",
            "查询业主自己发布的论坛帖子列表",
            buildParams(new String[][]{
                {"user_id", "integer", "用户ID", "true"},
                {"limit", "integer", "返回数量，默认10", "false"}
            })));

        tools.add(buildTool("cancel_repair",
            "撤回报修工单，仅限待审核状态的工单可撤回",
            buildParams(new String[][]{
                {"repair_id", "integer", "报修工单ID", "true"},
                {"owner_id", "integer", "业主用户ID，用于校验工单归属", "true"}
            })));

        tools.add(buildTool("query_repair_list",
            "管理员查询全部报修工单列表，可按状态筛选",
            buildParams(new String[][]{
                {"status", "integer", "筛选状态：0=待审核，1=已派单，2=维修中，3=已完成，不填=全部", "false"},
                {"limit", "integer", "返回数量，默认20", "false"}
            })));

        tools.add(buildTool("query_fee_list",
            "管理员查询全部缴费账单列表，可按状态筛选",
            buildParams(new String[][]{
                {"status", "integer", "筛选状态：0=未缴，1=已缴，不填=全部", "false"},
                {"limit", "integer", "返回数量，默认20", "false"}
            })));

        tools.add(buildTool("create_fee",
            "管理员为指定业主创建缴费账单",
            buildParams(new String[][]{
                {"owner_id", "integer", "业主用户ID", "true"},
                {"type", "string", "费用类型，如：物业费、水费、电费、燃气费", "true"},
                {"month", "string", "账单月份，如：2026-05", "true"},
                {"amount", "number", "缴费金额", "true"},
                {"remark", "string", "备注信息", "false"}
            })));

        tools.add(buildTool("query_feedback_list",
            "管理员查询业主提交的反馈列表",
            buildParams(new String[][]{
                {"status", "integer", "筛选状态：0=待处理，1=已处理，不填=全部", "false"},
                {"limit", "integer", "返回数量，默认20", "false"}
            })));

        tools.add(buildTool("process_feedback",
            "管理员处理业主反馈，标记为已处理并可添加回复",
            buildParams(new String[][]{
                {"feedback_id", "integer", "反馈ID", "true"},
                {"reply", "string", "回复内容", "false"}
            })));

        tools.add(buildTool("query_worker_stats",
            "查询维修员的绩效统计，包括完成工单数、平均评分等",
            buildParams(new String[][]{
                {"worker_id", "integer", "维修员用户ID", "true"}
            })));

        tools.add(buildTool("batch_create_fee",
            "管理员批量生成指定月份的物业费账单，为所有业主统一生成",
            buildParams(new String[][]{
                {"type", "string", "费用类型，如：物业费、水费", "true"},
                {"month", "string", "账单月份，如：2026-05", "true"},
                {"amount", "number", "每户缴费金额", "true"}
            })));

        return tools;
    }

    private static Map<String, Object> buildTool(String name, String description, Map<String, Object> parameters) {
        Map<String, Object> tool = new HashMap<>();
        tool.put("type", "function");
        Map<String, Object> function = new HashMap<>();
        function.put("name", name);
        function.put("description", description);
        function.put("parameters", parameters);
        tool.put("function", function);
        return tool;
    }

    private static Map<String, Object> buildParams(String[][] paramDefs) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        for (String[] def : paramDefs) {
            Map<String, Object> prop = new HashMap<>();
            prop.put("type", def[1]);
            prop.put("description", def[2]);
            properties.put(def[0], prop);
            if ("true".equals(def[3])) {
                required.add(def[0]);
            }
        }

        params.put("properties", properties);
        if (!required.isEmpty()) {
            params.put("required", required);
        }
        return params;
    }

    public String executeTool(String toolName, Map<String, Object> arguments) {
        try {
            switch (toolName) {
                case "query_unpaid_fees":
                    return queryUnpaidFees(arguments);
                case "query_fee_settings":
                    return queryFeeSettings();
                case "create_repair":
                    return createRepair(arguments);
                case "query_repair_status":
                    return queryRepairStatus(arguments);
                case "query_notices":
                    return queryNotices(arguments);
                case "query_owner_info":
                    return queryOwnerInfo(arguments);
                case "dispatch_repair":
                    return dispatchRepair(arguments);
                case "query_available_workers":
                    return queryAvailableWorkers();
                case "evaluate_repair":
                    return evaluateRepair(arguments);
                case "query_activities":
                    return queryActivities(arguments);
                case "submit_feedback":
                    return submitFeedback(arguments);
                case "query_dashboard_stats":
                    return queryDashboardStats();
                case "urge_payment":
                    return urgePayment();
                case "publish_notice":
                    return publishNotice(arguments);
                case "create_forum_post":
                    return createForumPost(arguments);
                case "search_forum_posts":
                    return searchForumPosts(arguments);
                case "signup_activity":
                    return signupActivity(arguments);
                case "query_notifications":
                    return queryNotifications(arguments);
                case "mark_notification_read":
                    return markNotificationRead(arguments);
                case "cancel_activity_signup":
                    return cancelActivitySignup(arguments);
                case "query_my_activities":
                    return queryMyActivities(arguments);
                case "query_fee_history":
                    return queryFeeHistory(arguments);
                case "query_my_forum_posts":
                    return queryMyForumPosts(arguments);
                case "cancel_repair":
                    return cancelRepair(arguments);
                case "query_repair_list":
                    return queryRepairList(arguments);
                case "query_fee_list":
                    return queryFeeList(arguments);
                case "create_fee":
                    return createFee(arguments);
                case "query_feedback_list":
                    return queryFeedbackList(arguments);
                case "process_feedback":
                    return processFeedback(arguments);
                case "query_worker_stats":
                    return queryWorkerStats(arguments);
                case "batch_create_fee":
                    return batchCreateFee(arguments);
                default:
                    return "{\"error\": \"未知工具: " + toolName + "\"}";
            }
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    private Long getLong(Map<String, Object> args, String key) {
        Object val = args.get(key);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        return Long.valueOf(val.toString());
    }

    private int getInt(Map<String, Object> args, String key, int defaultVal) {
        if (!args.containsKey(key) || args.get(key) == null) return defaultVal;
        return ((Number) args.get(key)).intValue();
    }

    private String getString(Map<String, Object> args, String key) {
        Object val = args.get(key);
        return val != null ? val.toString() : null;
    }

    private String queryUnpaidFees(Map<String, Object> args) {
        Long ownerId = getLong(args, "owner_id");
        List<BusFee> unpaidFees = busFeeMapper.selectUnpaid(ownerId);

        if (unpaidFees == null || unpaidFees.isEmpty()) {
            return "{\"result\": \"该业主没有欠费记录\", \"total\": 0, \"items\": []}";
        }

        BigDecimal total = BigDecimal.ZERO;
        StringBuilder details = new StringBuilder();
        List<Map<String, Object>> items = new ArrayList<>();
        for (BusFee fee : unpaidFees) {
            BigDecimal amount = fee.getAmount() != null ? fee.getAmount() : BigDecimal.ZERO;
            total = total.add(amount);
            details.append(fee.getMonth()).append(" ").append(fee.getType()).append(": ").append(amount).append("元; ");

            Map<String, Object> item = new HashMap<>();
            item.put("fee_id", fee.getId());
            item.put("type", fee.getType());
            item.put("month", fee.getMonth());
            item.put("amount", amount);
            items.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "查询成功");
        result.put("details", details.toString());
        result.put("total", total);
        result.put("count", unpaidFees.size());
        result.put("items", items);
        return JSONUtil.toJsonStr(result);
    }

    private String queryFeeSettings() {
        List<BusFeeSettings> settings = feeSettingsMapper.selectList(null);
        if (settings == null || settings.isEmpty()) {
            return "{\"result\": \"暂无收费标准信息\"}";
        }

        BusFeeSettings s = settings.get(0);
        return "{\"result\": \"查询成功\", \"property_fee\": " + s.getPropertyFee()
             + ", \"water_fee\": " + s.getWaterFee()
             + ", \"electricity_fee\": " + s.getElectricityFee()
             + ", \"gas_fee\": " + s.getGasFee() + "}";
    }

    private String createRepair(Map<String, Object> args) {
        Long ownerId = getLong(args, "owner_id");
        String content = getString(args, "content");

        BusRepair repair = new BusRepair();
        repair.setOwnerId(ownerId);
        repair.setContent(content);
        repair.setStatus(0);
        repair.setCreateTime(new Date());
        busRepairMapper.insert(repair);

        Map<String, Object> result = new HashMap<>();
        result.put("result", "报修单创建成功");
        result.put("repair_id", repair.getId());
        result.put("status", "待审核");
        result.put("content", content);
        return JSONUtil.toJsonStr(result);
    }

    private String queryRepairStatus(Map<String, Object> args) {
        Long ownerId = getLong(args, "owner_id");

        QueryWrapper<BusRepair> wrapper = new QueryWrapper<BusRepair>()
                .eq("owner_id", ownerId)
                .orderByDesc("create_time")
                .last("LIMIT 5");
        List<BusRepair> repairs = busRepairMapper.selectList(wrapper);

        if (repairs == null || repairs.isEmpty()) {
            return "{\"result\": \"该业主暂无报修记录\"}";
        }

        String[] statusNames = {"待审核", "已派单", "维修中", "已完成"};
        StringBuilder details = new StringBuilder();
        for (BusRepair repair : repairs) {
            String statusName = repair.getStatus() != null && repair.getStatus() < statusNames.length
                    ? statusNames[repair.getStatus()] : "未知";
            details.append("工单#").append(repair.getId())
                   .append(": ").append(repair.getContent())
                   .append(" [").append(statusName).append("]; ");
        }

        return "{\"result\": \"查询成功\", \"details\": \"" + details.toString()
             + "\", \"count\": " + repairs.size() + "}";
    }

    private String queryNotices(Map<String, Object> args) {
        int limit = getInt(args, "limit", 5);

        List<BusNotice> notices = busNoticeMapper.selectList(
                new QueryWrapper<BusNotice>()
                        .orderByDesc("publish_time")
                        .last("LIMIT " + limit)
        );

        if (notices == null || notices.isEmpty()) {
            return "{\"result\": \"暂无通知公告\"}";
        }

        StringBuilder details = new StringBuilder();
        for (BusNotice notice : notices) {
            String typeStr = notice.getType() != null && notice.getType() == 1 ? "[紧急]" : "";
            details.append(typeStr).append(notice.getTitle()).append("; ");
        }

        return "{\"result\": \"查询成功\", \"details\": \"" + details.toString()
             + "\", \"count\": " + notices.size() + "}";
    }

    private String queryOwnerInfo(Map<String, Object> args) {
        Long ownerId = getLong(args, "owner_id");

        SysUser user = sysUserMapper.selectById(ownerId);
        if (user == null) {
            return "{\"error\": \"用户不存在\"}";
        }

        SysOwner owner = sysOwnerMapper.selectByUserId(ownerId);
        String building = owner != null ? owner.getBuilding() : "未知";
        String unit = owner != null ? owner.getUnit() : "未知";
        String room = owner != null ? owner.getRoom() : "未知";

        return "{\"result\": \"查询成功\", \"real_name\": \"" + user.getRealName()
             + "\", \"phone\": \"" + user.getPhone()
             + "\", \"building\": \"" + building
             + "\", \"unit\": \"" + unit
             + "\", \"room\": \"" + room + "\"}";
    }

    private String dispatchRepair(Map<String, Object> args) {
        Long repairId = getLong(args, "repair_id");
        Long workerId = getLong(args, "worker_id");

        BusRepair repair = busRepairMapper.selectById(repairId);
        if (repair == null) {
            return "{\"error\": \"报修工单不存在\"}";
        }
        if (repair.getStatus() != null && repair.getStatus() != 0) {
            String[] statusNames = {"待审核", "已派单", "维修中", "已完成"};
            String currentStatus = repair.getStatus() < statusNames.length ? statusNames[repair.getStatus()] : "未知";
            return "{\"error\": \"该工单当前状态为" + currentStatus + "，无法派单\"}";
        }

        SysUser worker = sysUserMapper.selectById(workerId);
        if (worker == null) {
            return "{\"error\": \"维修员不存在\"}";
        }

        repair.setStatus(1);
        repair.setWorkerId(workerId);
        repair.setUpdateTime(new Date());
        busRepairMapper.updateById(repair);

        return "{\"result\": \"派单成功\", \"repair_id\": " + repairId
             + ", \"worker_id\": " + workerId
             + ", \"worker_name\": \"" + worker.getRealName() + "\"}";
    }

    private String queryAvailableWorkers() {
        List<SysRepairWorker> workers = sysRepairWorkerMapper.selectList(
                new QueryWrapper<SysRepairWorker>().eq("status", 1)
        );

        if (workers == null || workers.isEmpty()) {
            return "{\"result\": \"暂无可用维修员\"}";
        }

        StringBuilder details = new StringBuilder();
        for (SysRepairWorker worker : workers) {
            SysUser user = sysUserMapper.selectById(worker.getUserId());
            if (user != null) {
                details.append("ID:").append(user.getId())
                       .append(" 姓名:").append(user.getRealName())
                       .append(" 电话:").append(user.getPhone()).append("; ");
            }
        }

        return "{\"result\": \"查询成功\", \"details\": \"" + details.toString()
             + "\", \"count\": " + workers.size() + "}";
    }

    private String evaluateRepair(Map<String, Object> args) {
        Long ownerId = getLong(args, "owner_id");
        int score = getInt(args, "score", 5);
        String comment = getString(args, "comment");

        Long repairId = null;
        if (args.containsKey("repair_id") && args.get("repair_id") != null) {
            repairId = getLong(args, "repair_id");
        }

        if (repairId == null) {
            List<BusRepair> completedRepairs = busRepairMapper.selectList(
                    new QueryWrapper<BusRepair>()
                            .eq("owner_id", ownerId)
                            .eq("status", 3)
                            .orderByDesc("update_time")
                            .last("LIMIT 5")
            );

            if (completedRepairs == null || completedRepairs.isEmpty()) {
                return "{\"error\": \"您没有已完成的报修工单，无法评价\"}";
            }

            for (BusRepair r : completedRepairs) {
                BusEvaluation existingEval = busEvaluationMapper.selectOne(
                        new QueryWrapper<BusEvaluation>().eq("repair_id", r.getId())
                );
                if (existingEval == null) {
                    repairId = r.getId();
                    break;
                }
            }

            if (repairId == null) {
                return "{\"error\": \"您所有已完成的工单都已评价过了\"}";
            }
        }

        BusRepair repair = busRepairMapper.selectById(repairId);
        if (repair == null) {
            return "{\"error\": \"报修工单不存在\"}";
        }
        if (repair.getStatus() == null || repair.getStatus() != 3) {
            return "{\"error\": \"该工单尚未完成，无法评价\"}";
        }

        BusEvaluation existingEval = busEvaluationMapper.selectOne(
                new QueryWrapper<BusEvaluation>().eq("repair_id", repairId)
        );
        if (existingEval != null) {
            return "{\"error\": \"该工单已评价，不能重复评价\"}";
        }

        BusEvaluation evaluation = new BusEvaluation();
        evaluation.setRepairId(repairId);
        evaluation.setScore(Math.max(1, Math.min(5, score)));
        evaluation.setComment(comment != null ? comment : "");
        evaluation.setCreateTime(new Date());
        busEvaluationMapper.insert(evaluation);

        return "{\"result\": \"评价成功\", \"repair_id\": " + repairId
             + ", \"content\": \"" + repair.getContent() + "\""
             + ", \"score\": " + evaluation.getScore()
             + ", \"comment\": \"" + (comment != null ? comment : "") + "\"}";
    }

    private String queryActivities(Map<String, Object> args) {
        int limit = getInt(args, "limit", 5);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        List<BusActivity> activities = busActivityMapper.selectList(
                new QueryWrapper<BusActivity>()
                        .orderByDesc("start_time")
                        .last("LIMIT " + limit)
        );

        if (activities == null || activities.isEmpty()) {
            return "{\"result\": \"暂无社区活动\"}";
        }

        StringBuilder details = new StringBuilder();
        for (BusActivity activity : activities) {
            details.append(activity.getTitle());
            if (activity.getStartTime() != null) {
                details.append(" (时间: ").append(dateFormat.format(activity.getStartTime())).append(")");
            }
            if (activity.getLocation() != null) {
                details.append(" (地点: ").append(activity.getLocation()).append(")");
            }
            details.append("; ");
        }

        return "{\"result\": \"查询成功\", \"details\": \"" + details.toString()
             + "\", \"count\": " + activities.size() + "}";
    }

    private String submitFeedback(Map<String, Object> args) {
        Long userId = getLong(args, "user_id");
        String userName = getString(args, "user_name");
        String content = getString(args, "content");

        BusFeedback feedback = new BusFeedback();
        feedback.setUserId(userId.toString());
        feedback.setUserName(userName != null ? userName : "");
        feedback.setContent(content);
        feedback.setStatus(0);
        feedback.setCreateTime(new Date());
        busFeedbackMapper.insert(feedback);

        return "{\"result\": \"反馈提交成功\", \"feedback_id\": " + feedback.getId()
             + ", \"status\": \"待处理\"}";
    }

    private String queryDashboardStats() {
        long totalUsers = sysUserMapper.selectCount(
                new QueryWrapper<SysUser>().eq("role", 1));
        long totalWorkers = sysUserMapper.selectCount(
                new QueryWrapper<SysUser>().eq("role", 2));

        long pendingRepairs = busRepairMapper.selectCount(
                new QueryWrapper<BusRepair>().eq("status", 0));
        long processingRepairs = busRepairMapper.selectCount(
                new QueryWrapper<BusRepair>().in("status", Arrays.asList(1, 2)));
        long completedRepairs = busRepairMapper.selectCount(
                new QueryWrapper<BusRepair>().eq("status", 3));

        long unpaidFees = busFeeMapper.selectCount(
                new QueryWrapper<BusFee>().eq("status", 0));
        long paidFees = busFeeMapper.selectCount(
                new QueryWrapper<BusFee>().eq("status", 1));

        long totalFees = unpaidFees + paidFees;
        String paymentRate = totalFees > 0
                ? String.format("%.1f", (double) paidFees / totalFees * 100) : "0.0";

        long totalNotices = busNoticeMapper.selectCount(null);
        long totalActivities = busActivityMapper.selectCount(null);

        Map<String, Object> result = new HashMap<>();
        result.put("result", "查询成功");
        result.put("total_owners", totalUsers);
        result.put("total_workers", totalWorkers);
        result.put("pending_repairs", pendingRepairs);
        result.put("processing_repairs", processingRepairs);
        result.put("completed_repairs", completedRepairs);
        result.put("unpaid_fees_count", unpaidFees);
        result.put("paid_fees_count", paidFees);
        result.put("payment_rate", paymentRate + "%");
        result.put("total_notices", totalNotices);
        result.put("total_activities", totalActivities);
        return JSONUtil.toJsonStr(result);
    }

    private String urgePayment() {
        List<BusFee> unpaidFees = busFeeMapper.selectList(
                new QueryWrapper<BusFee>().eq("status", 0).eq("reminded", 0)
        );

        if (unpaidFees == null || unpaidFees.isEmpty()) {
            return "{\"result\": \"没有需要催缴的费用\"}";
        }

        int urgedCount = 0;
        for (BusFee fee : unpaidFees) {
            fee.setReminded(1);
            busFeeMapper.updateById(fee);
            urgedCount++;
        }

        return "{\"result\": \"催缴成功\", \"urged_count\": " + urgedCount
             + ", \"message\": \"已向" + urgedCount + "位业主发送催缴提醒\"}";
    }

    private String publishNotice(Map<String, Object> args) {
        String title = getString(args, "title");
        String content = getString(args, "content");
        int type = getInt(args, "type", 0);

        if (title == null || title.trim().isEmpty()) {
            return "{\"error\": \"通知标题不能为空\"}";
        }
        if (content == null || content.trim().isEmpty()) {
            return "{\"error\": \"通知内容不能为空\"}";
        }

        BusNotice notice = new BusNotice();
        notice.setTitle(title.trim());
        notice.setContent(content.trim());
        notice.setType(type);
        notice.setPublishTime(new Date());
        busNoticeMapper.insert(notice);

        String typeStr = type == 1 ? "紧急" : "普通";
        return "{\"result\": \"通知发布成功\", \"notice_id\": " + notice.getId()
             + ", \"title\": \"" + title.trim() + "\""
             + ", \"type\": \"" + typeStr + "\"}";
    }

    private String createForumPost(Map<String, Object> args) {
        Long userId = getLong(args, "user_id");
        String title = getString(args, "title");
        String content = getString(args, "content");
        String categoryName = getString(args, "category_name");

        if (title == null || title.trim().isEmpty()) {
            return "{\"error\": \"帖子标题不能为空\"}";
        }
        if (content == null || content.trim().isEmpty()) {
            return "{\"error\": \"帖子内容不能为空\"}";
        }

        Long categoryId = null;
        List<BusForumCategory> categories = busForumCategoryMapper.selectList(
                new QueryWrapper<BusForumCategory>().orderByAsc("sort_order")
        );

        if (categoryName != null && !categoryName.trim().isEmpty()) {
            for (BusForumCategory cat : categories) {
                if (cat.getName().equals(categoryName.trim())) {
                    categoryId = cat.getId();
                    break;
                }
            }
            if (categoryId == null) {
                for (BusForumCategory cat : categories) {
                    if (cat.getName().contains(categoryName.trim()) || categoryName.trim().contains(cat.getName())) {
                        categoryId = cat.getId();
                        break;
                    }
                }
            }
        }

        if (categoryId == null && !categories.isEmpty()) {
            String titleLower = title.trim();
            String contentLower = content.trim();
            for (BusForumCategory cat : categories) {
                String catName = cat.getName();
                String catDesc = cat.getDescription() != null ? cat.getDescription() : "";
                if (titleLower.contains(catName) || contentLower.contains(catName)
                        || titleLower.contains(catDesc) || contentLower.contains(catDesc)) {
                    categoryId = cat.getId();
                    break;
                }
            }
        }

        if (categoryId == null && !categories.isEmpty()) {
            categoryId = categories.get(0).getId();
        }

        if (categoryId == null) {
            return "{\"error\": \"论坛暂无分区，无法发帖\"}";
        }

        String matchedCategoryName = "";
        for (BusForumCategory cat : categories) {
            if (cat.getId().equals(categoryId)) {
                matchedCategoryName = cat.getName();
                break;
            }
        }

        BusForum post = new BusForum();
        post.setUserId(userId);
        post.setCategoryId(categoryId);
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setIsPublic(1);
        post.setIsPinned(0);
        post.setCreateTime(new Date());
        busForumMapper.insert(post);

        return "{\"result\": \"发帖成功\", \"post_id\": " + post.getId()
             + ", \"title\": \"" + title.trim() + "\""
             + ", \"category\": \"" + matchedCategoryName + "\"}";
    }

    private String searchForumPosts(Map<String, Object> args) {
        String keyword = getString(args, "keyword");
        int limit = getInt(args, "limit", 5);

        if (keyword == null || keyword.trim().isEmpty()) {
            return "{\"error\": \"搜索关键词不能为空\"}";
        }

        List<BusForum> posts = busForumMapper.selectList(
                new QueryWrapper<BusForum>()
                        .and(w -> w.like("title", keyword).or().like("content", keyword))
                        .orderByDesc("is_pinned")
                        .orderByDesc("create_time")
                        .last("LIMIT " + limit)
        );

        if (posts == null || posts.isEmpty()) {
            return "{\"result\": \"没有找到相关帖子\", \"keyword\": \"" + keyword.trim() + "\"}";
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (BusForum post : posts) {
            String userName = "";
            if (post.getUserId() != null) {
                SysUser user = sysUserMapper.selectById(post.getUserId());
                if (user != null) userName = user.getRealName() != null ? user.getRealName() : user.getUsername();
            }

            String catName = "";
            if (post.getCategoryId() != null) {
                BusForumCategory cat = busForumCategoryMapper.selectById(post.getCategoryId());
                if (cat != null) catName = cat.getName();
            }

            Map<String, Object> item = new HashMap<>();
            item.put("post_id", post.getId());
            item.put("title", post.getTitle());
            item.put("category", catName);
            item.put("author", userName);
            item.put("is_pinned", post.getIsPinned());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            item.put("create_time", post.getCreateTime() != null ? sdf.format(post.getCreateTime()) : "");
            items.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "搜索成功");
        result.put("keyword", keyword.trim());
        result.put("count", posts.size());
        result.put("items", items);
        return JSONUtil.toJsonStr(result);
    }

    private String signupActivity(Map<String, Object> args) {
        Long userId = getLong(args, "user_id");
        Long activityId = getLong(args, "activity_id");

        BusActivity activity = busActivityMapper.selectById(activityId);
        if (activity == null) {
            return "{\"error\": \"活动不存在\"}";
        }

        Long existing = busActivitySignupMapper.selectCount(
            new QueryWrapper<BusActivitySignup>()
                .eq("activity_id", activityId)
                .eq("user_id", userId)
        );
        if (existing > 0) {
            return "{\"error\": \"您已报名该活动\"}";
        }

        BusActivitySignup signup = new BusActivitySignup();
        signup.setActivityId(activityId);
        signup.setUserId(userId);
        signup.setCreateTime(new Date());
        busActivitySignupMapper.insert(signup);

        Long count = busActivitySignupMapper.countByActivityId(activityId);
        return "{\"result\": \"报名成功\", \"activity\": \"" + activity.getTitle()
             + "\", \"signup_count\": " + count + "}";
    }

    private String queryNotifications(Map<String, Object> args) {
        Long userId = getLong(args, "user_id");
        int limit = getInt(args, "limit", 5);

        List<SysNotification> notifications = sysNotificationMapper.selectList(
            new QueryWrapper<SysNotification>()
                .eq("user_id", userId)
                .orderByDesc("create_time")
                .last("LIMIT " + limit)
        );

        if (notifications == null || notifications.isEmpty()) {
            return "{\"result\": \"暂无通知消息\"}";
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (SysNotification n : notifications) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", n.getId());
            item.put("title", n.getTitle());
            item.put("content", n.getContent());
            item.put("type", n.getType());
            item.put("is_read", n.getIsRead());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            item.put("create_time", n.getCreateTime() != null ? sdf.format(n.getCreateTime()) : "");
            items.add(item);
        }

        long unreadCount = sysNotificationMapper.countUnread(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("result", "查询成功");
        result.put("unread_count", unreadCount);
        result.put("count", notifications.size());
        result.put("items", items);
        return JSONUtil.toJsonStr(result);
    }

    private String markNotificationRead(Map<String, Object> args) {
        Long notificationId = getLong(args, "notification_id");

        SysNotification notification = sysNotificationMapper.selectById(notificationId);
        if (notification == null) {
            return "{\"error\": \"通知不存在\"}";
        }

        notification.setIsRead(1);
        sysNotificationMapper.updateById(notification);
        return "{\"result\": \"已标记已读\", \"notification_id\": " + notificationId + "}";
    }

    private String cancelActivitySignup(Map<String, Object> args) {
        Long userId = getLong(args, "user_id");
        Long activityId = getLong(args, "activity_id");

        BusActivitySignup signup = busActivitySignupMapper.selectOne(
            new QueryWrapper<BusActivitySignup>()
                .eq("activity_id", activityId)
                .eq("user_id", userId)
        );
        if (signup == null) {
            return "{\"error\": \"您未报名该活动，无法取消\"}";
        }

        busActivitySignupMapper.deleteById(signup.getId());

        BusActivity activity = busActivityMapper.selectById(activityId);
        String activityTitle = activity != null ? activity.getTitle() : "未知活动";
        Long count = busActivitySignupMapper.countByActivityId(activityId);
        return "{\"result\": \"取消报名成功\", \"activity\": \"" + activityTitle
             + "\", \"remaining_signup_count\": " + count + "}";
    }

    private String queryMyActivities(Map<String, Object> args) {
        Long userId = getLong(args, "user_id");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        List<BusActivitySignup> signups = busActivitySignupMapper.selectList(
            new QueryWrapper<BusActivitySignup>()
                .eq("user_id", userId)
                .orderByDesc("create_time")
        );

        if (signups == null || signups.isEmpty()) {
            return "{\"result\": \"您尚未报名任何活动\", \"count\": 0, \"items\": []}";
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (BusActivitySignup signup : signups) {
            BusActivity activity = busActivityMapper.selectById(signup.getActivityId());
            if (activity != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("activity_id", activity.getId());
                item.put("title", activity.getTitle());
                item.put("location", activity.getLocation() != null ? activity.getLocation() : "");
                item.put("start_time", activity.getStartTime() != null ? sdf.format(activity.getStartTime()) : "");
                item.put("signup_time", signup.getCreateTime() != null ? sdf.format(signup.getCreateTime()) : "");
                items.add(item);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "查询成功");
        result.put("count", items.size());
        result.put("items", items);
        return JSONUtil.toJsonStr(result);
    }

    private String queryFeeHistory(Map<String, Object> args) {
        Long ownerId = getLong(args, "owner_id");
        Integer status = args.containsKey("status") && args.get("status") != null
            ? ((Number) args.get("status")).intValue() : null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        QueryWrapper<BusFee> wrapper = new QueryWrapper<BusFee>()
            .eq("owner_id", ownerId)
            .orderByDesc("create_time");
        if (status != null) {
            wrapper.eq("status", status);
        }

        List<BusFee> fees = busFeeMapper.selectList(wrapper);

        if (fees == null || fees.isEmpty()) {
            String filterDesc = status != null ? (status == 0 ? "未缴" : "已缴") : "";
            return "{\"result\": \"暂无" + filterDesc + "缴费记录\", \"count\": 0, \"items\": []}";
        }

        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalUnpaid = BigDecimal.ZERO;
        List<Map<String, Object>> items = new ArrayList<>();
        for (BusFee fee : fees) {
            BigDecimal amount = fee.getAmount() != null ? fee.getAmount() : BigDecimal.ZERO;
            if (fee.getStatus() != null && fee.getStatus() == 1) {
                totalPaid = totalPaid.add(amount);
            } else {
                totalUnpaid = totalUnpaid.add(amount);
            }

            Map<String, Object> item = new HashMap<>();
            item.put("fee_id", fee.getId());
            item.put("type", fee.getType());
            item.put("month", fee.getMonth());
            item.put("amount", amount);
            item.put("status", fee.getStatus() == 1 ? "已缴" : "未缴");
            item.put("pay_time", fee.getPayTime() != null ? sdf.format(fee.getPayTime()) : "");
            item.put("create_time", fee.getCreateTime() != null ? sdf.format(fee.getCreateTime()) : "");
            if (fee.getRemark() != null && !fee.getRemark().isEmpty()) {
                item.put("remark", fee.getRemark());
            }
            items.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "查询成功");
        result.put("count", items.size());
        result.put("total_paid", totalPaid);
        result.put("total_unpaid", totalUnpaid);
        result.put("items", items);
        return JSONUtil.toJsonStr(result);
    }

    private String queryMyForumPosts(Map<String, Object> args) {
        Long userId = getLong(args, "user_id");
        int limit = getInt(args, "limit", 10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        List<BusForum> posts = busForumMapper.selectList(
            new QueryWrapper<BusForum>()
                .eq("user_id", userId)
                .orderByDesc("create_time")
                .last("LIMIT " + limit)
        );

        if (posts == null || posts.isEmpty()) {
            return "{\"result\": \"您尚未发布过帖子\", \"count\": 0, \"items\": []}";
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (BusForum post : posts) {
            String catName = "";
            if (post.getCategoryId() != null) {
                BusForumCategory cat = busForumCategoryMapper.selectById(post.getCategoryId());
                if (cat != null) catName = cat.getName();
            }

            Map<String, Object> item = new HashMap<>();
            item.put("post_id", post.getId());
            item.put("title", post.getTitle());
            item.put("category", catName);
            item.put("is_pinned", post.getIsPinned());
            item.put("create_time", post.getCreateTime() != null ? sdf.format(post.getCreateTime()) : "");
            items.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "查询成功");
        result.put("count", items.size());
        result.put("items", items);
        return JSONUtil.toJsonStr(result);
    }

    private String cancelRepair(Map<String, Object> args) {
        Long repairId = getLong(args, "repair_id");
        Long ownerId = getLong(args, "owner_id");

        BusRepair repair = busRepairMapper.selectById(repairId);
        if (repair == null) {
            return "{\"error\": \"报修工单不存在\"}";
        }
        if (!repair.getOwnerId().equals(ownerId)) {
            return "{\"error\": \"该工单不属于您，无法撤回\"}";
        }
        if (repair.getStatus() != null && repair.getStatus() != 0) {
            String[] statusNames = {"待审核", "已派单", "维修中", "已完成"};
            String currentStatus = repair.getStatus() < statusNames.length ? statusNames[repair.getStatus()] : "未知";
            return "{\"error\": \"该工单当前状态为" + currentStatus + "，只有待审核状态可以撤回\"}";
        }

        busRepairMapper.deleteById(repairId);
        return "{\"result\": \"报修工单撤回成功\", \"repair_id\": " + repairId
             + ", \"content\": \"" + repair.getContent() + "\"}";
    }

    private String queryRepairList(Map<String, Object> args) {
        Integer status = args.containsKey("status") && args.get("status") != null
            ? ((Number) args.get("status")).intValue() : null;
        int limit = getInt(args, "limit", 20);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        QueryWrapper<BusRepair> wrapper = new QueryWrapper<BusRepair>()
            .orderByDesc("create_time")
            .last("LIMIT " + limit);
        if (status != null) {
            wrapper.eq("status", status);
        }

        List<BusRepair> repairs = busRepairMapper.selectList(wrapper);

        if (repairs == null || repairs.isEmpty()) {
            return "{\"result\": \"暂无报修工单\", \"count\": 0, \"items\": []}";
        }

        String[] statusNames = {"待审核", "已派单", "维修中", "已完成"};
        List<Map<String, Object>> items = new ArrayList<>();
        for (BusRepair repair : repairs) {
            String statusName = repair.getStatus() != null && repair.getStatus() < statusNames.length
                ? statusNames[repair.getStatus()] : "未知";

            String ownerName = "";
            if (repair.getOwnerId() != null) {
                SysUser user = sysUserMapper.selectById(repair.getOwnerId());
                if (user != null) ownerName = user.getRealName() != null ? user.getRealName() : user.getUsername();
            }

            String workerName = "";
            if (repair.getWorkerId() != null) {
                SysUser worker = sysUserMapper.selectById(repair.getWorkerId());
                if (worker != null) workerName = worker.getRealName() != null ? worker.getRealName() : worker.getUsername();
            }

            Map<String, Object> item = new HashMap<>();
            item.put("repair_id", repair.getId());
            item.put("content", repair.getContent());
            item.put("type", repair.getType() != null ? repair.getType() : "");
            item.put("status", statusName);
            item.put("owner_name", ownerName);
            item.put("worker_name", workerName);
            item.put("create_time", repair.getCreateTime() != null ? sdf.format(repair.getCreateTime()) : "");
            items.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "查询成功");
        result.put("count", items.size());
        result.put("items", items);
        return JSONUtil.toJsonStr(result);
    }

    private String queryFeeList(Map<String, Object> args) {
        Integer status = args.containsKey("status") && args.get("status") != null
            ? ((Number) args.get("status")).intValue() : null;
        int limit = getInt(args, "limit", 20);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        QueryWrapper<BusFee> wrapper = new QueryWrapper<BusFee>()
            .orderByDesc("create_time")
            .last("LIMIT " + limit);
        if (status != null) {
            wrapper.eq("status", status);
        }

        List<BusFee> fees = busFeeMapper.selectList(wrapper);

        if (fees == null || fees.isEmpty()) {
            return "{\"result\": \"暂无缴费账单\", \"count\": 0, \"items\": []}";
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (BusFee fee : fees) {
            String ownerName = "";
            if (fee.getOwnerId() != null) {
                SysUser user = sysUserMapper.selectById(fee.getOwnerId());
                if (user != null) ownerName = user.getRealName() != null ? user.getRealName() : user.getUsername();
            }

            Map<String, Object> item = new HashMap<>();
            item.put("fee_id", fee.getId());
            item.put("owner_name", ownerName);
            item.put("type", fee.getType());
            item.put("month", fee.getMonth());
            item.put("amount", fee.getAmount());
            item.put("status", fee.getStatus() == 1 ? "已缴" : "未缴");
            item.put("pay_time", fee.getPayTime() != null ? sdf.format(fee.getPayTime()) : "");
            item.put("create_time", fee.getCreateTime() != null ? sdf.format(fee.getCreateTime()) : "");
            items.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "查询成功");
        result.put("count", items.size());
        result.put("items", items);
        return JSONUtil.toJsonStr(result);
    }

    private String createFee(Map<String, Object> args) {
        Long ownerId = getLong(args, "owner_id");
        String type = getString(args, "type");
        String month = getString(args, "month");
        Object amountObj = args.get("amount");
        String remark = getString(args, "remark");

        if (type == null || type.trim().isEmpty()) {
            return "{\"error\": \"费用类型不能为空\"}";
        }
        if (month == null || month.trim().isEmpty()) {
            return "{\"error\": \"账单月份不能为空\"}";
        }
        if (amountObj == null) {
            return "{\"error\": \"缴费金额不能为空\"}";
        }

        BigDecimal amount;
        if (amountObj instanceof Number) {
            amount = new BigDecimal(amountObj.toString());
        } else {
            amount = new BigDecimal(amountObj.toString());
        }

        SysUser user = sysUserMapper.selectById(ownerId);
        if (user == null) {
            return "{\"error\": \"业主不存在\"}";
        }

        BusFee fee = new BusFee();
        fee.setOwnerId(ownerId);
        fee.setType(type.trim());
        fee.setMonth(month.trim());
        fee.setAmount(amount);
        fee.setStatus(0);
        fee.setReminded(0);
        if (remark != null && !remark.trim().isEmpty()) {
            fee.setRemark(remark.trim());
        }
        fee.setCreateTime(new Date());
        busFeeMapper.insert(fee);

        String ownerName = user.getRealName() != null ? user.getRealName() : user.getUsername();
        return "{\"result\": \"账单创建成功\", \"fee_id\": " + fee.getId()
             + ", \"owner\": \"" + ownerName
             + "\", \"type\": \"" + type.trim()
             + "\", \"month\": \"" + month.trim()
             + "\", \"amount\": " + amount + "}";
    }

    private String queryFeedbackList(Map<String, Object> args) {
        Integer status = args.containsKey("status") && args.get("status") != null
            ? ((Number) args.get("status")).intValue() : null;
        int limit = getInt(args, "limit", 20);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        QueryWrapper<BusFeedback> wrapper = new QueryWrapper<BusFeedback>()
            .orderByDesc("create_time")
            .last("LIMIT " + limit);
        if (status != null) {
            wrapper.eq("status", status);
        }

        List<BusFeedback> feedbacks = busFeedbackMapper.selectList(wrapper);

        if (feedbacks == null || feedbacks.isEmpty()) {
            return "{\"result\": \"暂无反馈记录\", \"count\": 0, \"items\": []}";
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (BusFeedback fb : feedbacks) {
            Map<String, Object> item = new HashMap<>();
            item.put("feedback_id", fb.getId());
            item.put("user_name", fb.getUserName() != null ? fb.getUserName() : "");
            item.put("content", fb.getContent());
            item.put("status", fb.getStatus() == 1 ? "已处理" : "待处理");
            item.put("reply", fb.getReply() != null ? fb.getReply() : "");
            item.put("create_time", fb.getCreateTime() != null ? sdf.format(fb.getCreateTime()) : "");
            items.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "查询成功");
        result.put("count", items.size());
        result.put("items", items);
        return JSONUtil.toJsonStr(result);
    }

    private String processFeedback(Map<String, Object> args) {
        Long feedbackId = getLong(args, "feedback_id");
        String reply = getString(args, "reply");

        BusFeedback feedback = busFeedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            return "{\"error\": \"反馈记录不存在\"}";
        }
        if (feedback.getStatus() != null && feedback.getStatus() == 1) {
            return "{\"error\": \"该反馈已处理，不能重复处理\"}";
        }

        feedback.setStatus(1);
        feedback.setUpdateTime(new Date());
        if (reply != null && !reply.trim().isEmpty()) {
            feedback.setReply(reply.trim());
        }
        busFeedbackMapper.updateById(feedback);

        return "{\"result\": \"反馈处理成功\", \"feedback_id\": " + feedbackId
             + ", \"user_name\": \"" + (feedback.getUserName() != null ? feedback.getUserName() : "")
             + "\", \"has_reply\": " + (reply != null && !reply.trim().isEmpty()) + "}";
    }

    private String queryWorkerStats(Map<String, Object> args) {
        Long workerId = getLong(args, "worker_id");

        SysUser worker = sysUserMapper.selectById(workerId);
        if (worker == null) {
            return "{\"error\": \"维修员不存在\"}";
        }

        SysRepairWorker repairWorker = sysRepairWorkerMapper.selectByUserId(workerId);
        String workerStatus = "未知";
        if (repairWorker != null) {
            if (repairWorker.getStatus() == 0) workerStatus = "待审核";
            else if (repairWorker.getStatus() == 1) workerStatus = "正常";
            else if (repairWorker.getStatus() == 2) workerStatus = "已禁用";
        }

        long completedCount = busRepairMapper.selectCount(
            new QueryWrapper<BusRepair>().eq("worker_id", workerId).eq("status", 3)
        );
        long processingCount = busRepairMapper.selectCount(
            new QueryWrapper<BusRepair>().eq("worker_id", workerId).in("status", Arrays.asList(1, 2))
        );

        List<BusRepair> completedRepairs = busRepairMapper.selectList(
            new QueryWrapper<BusRepair>().eq("worker_id", workerId).eq("status", 3)
        );

        double avgScore = 0;
        int evalCount = 0;
        if (completedRepairs != null && !completedRepairs.isEmpty()) {
            BigDecimal totalScore = BigDecimal.ZERO;
            for (BusRepair r : completedRepairs) {
                BusEvaluation eval = busEvaluationMapper.selectOne(
                    new QueryWrapper<BusEvaluation>().eq("repair_id", r.getId())
                );
                if (eval != null) {
                    totalScore = totalScore.add(new BigDecimal(eval.getScore()));
                    evalCount++;
                }
            }
            if (evalCount > 0) {
                avgScore = totalScore.doubleValue() / evalCount;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "查询成功");
        result.put("worker_name", worker.getRealName() != null ? worker.getRealName() : worker.getUsername());
        result.put("phone", worker.getPhone() != null ? worker.getPhone() : "");
        result.put("status", workerStatus);
        result.put("completed_count", completedCount);
        result.put("processing_count", processingCount);
        result.put("evaluation_count", evalCount);
        result.put("avg_score", evalCount > 0 ? String.format("%.1f", avgScore) : "暂无评价");
        return JSONUtil.toJsonStr(result);
    }

    private String batchCreateFee(Map<String, Object> args) {
        String type = getString(args, "type");
        String month = getString(args, "month");
        Object amountObj = args.get("amount");

        if (type == null || type.trim().isEmpty()) {
            return "{\"error\": \"费用类型不能为空\"}";
        }
        if (month == null || month.trim().isEmpty()) {
            return "{\"error\": \"账单月份不能为空\"}";
        }
        if (amountObj == null) {
            return "{\"error\": \"缴费金额不能为空\"}";
        }

        BigDecimal amount;
        if (amountObj instanceof Number) {
            amount = new BigDecimal(amountObj.toString());
        } else {
            amount = new BigDecimal(amountObj.toString());
        }

        List<SysOwner> owners = sysOwnerMapper.selectList(null);
        if (owners == null || owners.isEmpty()) {
            return "{\"error\": \"系统中没有业主信息\"}";
        }

        int createdCount = 0;
        int skippedCount = 0;
        for (SysOwner owner : owners) {
            Long ownerId = owner.getUserId();

            Long existing = busFeeMapper.selectCount(
                new QueryWrapper<BusFee>()
                    .eq("owner_id", ownerId)
                    .eq("type", type.trim())
                    .eq("month", month.trim())
            );
            if (existing > 0) {
                skippedCount++;
                continue;
            }

            BusFee fee = new BusFee();
            fee.setOwnerId(ownerId);
            fee.setType(type.trim());
            fee.setMonth(month.trim());
            fee.setAmount(amount);
            fee.setStatus(0);
            fee.setReminded(0);
            fee.setCreateTime(new Date());
            busFeeMapper.insert(fee);
            createdCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "批量生成完成");
        result.put("type", type.trim());
        result.put("month", month.trim());
        result.put("amount", amount);
        result.put("created_count", createdCount);
        result.put("skipped_count", skippedCount);
        result.put("total_owners", owners.size());
        return JSONUtil.toJsonStr(result);
    }
}
