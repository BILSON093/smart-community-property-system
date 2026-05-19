# AI Agent 智能助手 - 能力与技能文档

## 一、什么是 Agent？

Agent（智能体）是比普通AI客服更强大的AI助手。普通AI客服**只能回答问题**，而Agent**还能执行操作**。

| 对比项 | AI客服 `/ai/chat` | Agent `/agent/chat` |
|--------|-------------------|---------------------|
| 回答问题 | ✅ | ✅ |
| 查询数据 | ❌ 只能告诉你去哪查 | ✅ 直接帮你查 |
| 创建工单 | ❌ 只能说"请联系物业" | ✅ 直接帮你创建 |
| 缴纳费用 | ❌ 只能说"请前往缴费页面" | ✅ 直接帮你缴费 |
| 派单催缴 | ❌ | ✅ 管理员专属 |
| 工具调用 | 无 | 17个工具可调用 |

---

## 二、使用入口

### 管理员端（web-admin）
- 登录管理后台 → 侧边栏 → **AI智能助手**
- 路径：`/agent-chat`
- 拥有全部17个工具的权限

### 业主端（web-user）
- 登录业主网页 → 顶部导航栏 → **智能助手**
- 路径：`/home/chat`
- 顶部三个按钮可切换：**智能助手** / **AI客服** / **人工**
- 拥有14个业主工具的权限

### 业主端（微信小程序）
- 打开小程序 → 底部Tab **AI客服** → 点击按钮切换模式
- 三种模式：智能助手 → AI客服 → 人工客服
- 拥有14个业主工具的权限

### API接口
```
POST /api/agent/chat
Headers: Authorization: Bearer <token>
Body: {
  "message": "帮我看看系统概况",
  "history": []  // 可选，对话历史
}
```

---

## 三、技能清单（17个工具）

### 业主工具（14个）

| # | 工具名 | 功能 | 示例触发语 | 说明 |
|---|--------|------|-----------|------|
| 1 | `query_unpaid_fees` | 查询欠费 | "我欠了多少钱？" | 列出所有未缴费项目 |
| 2 | `query_fee_settings` | 查询收费标准 | "水费多少钱一吨？" | 物业费/水费/电费/燃气费单价 |
| 3 | `create_repair` | 创建报修 | "我家水管漏水了" | 自动创建报修工单 |
| 4 | `query_repair_status` | 查询报修进度 | "我的报修处理了吗？" | 显示最近5条报修记录 |
| 5 | `query_notices` | 查询通知 | "最近有什么通知？" | 最新5条通知 |
| 6 | `query_owner_info` | 查询业主信息 | "我的房号是多少？" | 姓名/电话/楼栋/单元/房间号 |
| 7 | `evaluate_repair` | 评价维修 | "给维修打个5分" | 不指定工单ID自动评价最近已完成工单 |
| 8 | `query_activities` | 查询活动 | "最近有什么活动？" | 活动标题/时间/地点 |
| 9 | `submit_feedback` | 提交反馈 | "我要投诉楼道灯不亮" | 提交意见反馈 |
| 10 | `create_forum_post` | 论坛发帖 | "帮我发个帖子" | 不指定分区时自动判断 |
| 11 | `search_forum_posts` | 搜索帖子 | "搜一下停水的帖子" | 模糊搜索标题和内容 |
| 12 | `signup_activity` | 报名活动 | "我要报名周末的活动" | 报名参加社区活动 |
| 13 | `query_notifications` | 查询通知 | "我有什么新消息？" | 查询推送通知 |
| 14 | `mark_notification_read` | 标记已读 | "把通知都标为已读" | 标记通知为已读 |

### 管理员专用工具（5个）

| # | 工具名 | 功能 | 示例触发语 | 说明 |
|---|--------|------|-----------|------|
| 15 | `dispatch_repair` | 派单 | "把工单5派给张师傅" | 仅待审核工单可派单 |
| 16 | `query_available_workers` | 查询维修员 | "有哪些维修员空闲？" | 列出状态正常的维修员 |
| 17 | `query_dashboard_stats` | 系统概览 | "帮我看看系统概况" | 业主数/报修统计/缴费率等 |
| 18 | `urge_payment` | 一键催缴 | "催一下没交费的业主" | 批量标记催缴提醒 |
| 19 | `publish_notice` | 一键发通知 | "发个通知：明天停水" | 创建通知公告，可设普通/紧急 |

---

## 四、典型对话场景

### 场景1：业主报修
```
业主: 我家厨房水管漏水了，很急！
Agent: [调用 create_repair]
       已为您创建报修单，单号#15，状态：待审核。
       管理员将尽快派维修员处理，请耐心等待。
```

### 场景2：业主缴费
```
业主: 我欠了多少钱？
Agent: [调用 query_unpaid_fees]
       您目前有3笔未缴费用：
       - 2024-01 水费: 45.00元
       - 2024-01 电费: 120.50元
       - 2024-01 物业费: 200.00元
       共计365.50元。需要帮您缴纳吗？
```

### 场景3：管理员派单
```
管理员: 帮我派一下报修单
Agent: [调用 query_available_workers]
       当前可用维修员：
       - ID:3 张师傅 电话:138xxxx
       - ID:5 李师傅 电话:139xxxx
       请问派给哪位？

管理员: 派给张师傅，工单15
Agent: [调用 dispatch_repair(repair_id=15, worker_id=3)]
       派单成功！工单#15已派给张师傅(138xxxx)。
```

### 场景4：管理员发通知
```
管理员: 发个通知，明天上午8点到12点停水维修
Agent: [调用 publish_notice(title="停水通知", content="明天上午8点到12点停水维修", type=0)]
       通知发布成功！标题：停水通知，类型：普通。
```

### 场景5：业主评价
```
业主: 给维修打个5分，修得很好
Agent: [调用 evaluate_repair(score=5, comment="修得很好")]
       评价成功！工单#10（水管漏水）评分5分，感谢您的反馈！
```

### 场景6：业主论坛发帖
```
业主: 帮我发个帖子，我家猫走丢了，在3号楼附近
Agent: [调用 create_forum_post(title="寻猫启事", content="我家猫走丢了，在3号楼附近，有看到的联系我")]
       发帖成功！帖子"寻猫启事"已发布在【生活互助】分区。
```

### 场景7：业主报名活动
```
业主: 我要报名周末的社区运动会
Agent: [调用 signup_activity]
       报名成功！已为您报名社区运动会。
```

### 场景8：管理员查看系统概况
```
管理员: 系统概况怎么样？
Agent: [调用 query_dashboard_stats]
       系统概况：
       - 业主总数：120人
       - 维修员：5人
       - 待审核报修：3单
       - 维修中：2单
       - 已完成：48单
       - 缴费率：87.5%
```

---

## 五、权限控制

Agent根据JWT Token中的用户角色自动控制可用工具：

| 角色 | role值 | 可用工具数 | 说明 |
|------|--------|-----------|------|
| 管理员 | 0 | 17个（全部） | 业主工具 + 管理员工具 |
| 业主 | 1 | 14个 | 业主工具 |
| 维修员 | 2 | 0个 | 维修员不使用Agent |

> 业主调用管理员工具时，Agent不会将该工具发送给LLM，确保权限安全。

---

## 六、技术架构

```
用户消息
   │
   ▼
AgentController (/agent/chat)
   │ 提取 userId (from JWT)
   ▼
PropertyAgent.chat(userId, message, history)
   │
   ├─ 根据userId查询role，过滤工具列表
   ├─ 构建系统提示词（含用户ID和可用工具说明）
   │
   ▼
LLM API (OpenAI Function Calling协议)
   │
   ├─ LLM返回tool_calls → 执行工具 → 结果回传LLM → 最多5轮
   └─ LLM返回content → 最终回复
   │
   ▼
返回 { reply, tool_calls, rounds }
```

### 核心文件

| 文件 | 位置 | 作用 |
|------|------|------|
| PropertyAgentTools.java | backend/.../agent | 17个工具定义与执行 |
| PropertyAgent.java | backend/.../agent | Agent核心（工具调用循环） |
| AgentController.java | backend/.../agent | REST API接口 |
| AgentChat.vue | web-admin/src/views | 管理员对话页面 |
| Chat.vue | web-user/src/views | 业主Web对话页面 |
| chat.js / chat.wxml | miniprogram-owner/pages/chat | 小程序对话页面 |

---

## 七、配置要求

1. **AI模型需支持Function Calling**：推荐 Qwen系列、GPT-4、GPT-3.5-turbo 等
2. **AI配置**：`ai_config` 表中需有 `enabled=1` 的记录，或在 `application.yml` 中配置默认地址
3. **Agent配置**（application.yml）：
   ```yaml
   agent:
     max-tool-rounds: 5    # 最大工具调用轮数
     max-history: 10       # 最大对话历史条数
   ```
