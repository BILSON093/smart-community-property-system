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
| 工具调用 | 无 | 31个工具可调用 |

---

## 二、使用入口

### 管理员端（web-admin）
- 登录管理后台 → 侧边栏 → **AI智能助手**
- 路径：`/agent-chat`
- 拥有全部31个工具的权限

### 业主端（web-user）
- 登录业主网页 → 顶部导航栏 → **智能助手**
- 路径：`/home/chat`
- 顶部三个按钮可切换：**智能助手** / **AI客服** / **人工**
- 拥有19个业主工具的权限

### 业主端（微信小程序）
- 打开小程序 → 底部Tab **AI客服** → 点击按钮切换模式
- 三种模式：智能助手 → AI客服 → 人工客服
- 拥有19个业主工具的权限

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

## 三、技能清单（31个工具）

### 业主工具（19个）

| # | 工具名 | 功能 | 示例触发语 | 说明 |
|---|--------|------|-----------|------|
| 1 | `query_unpaid_fees` | 查询欠费 | "我欠了多少钱？" | 列出所有未缴费项目 |
| 2 | `query_fee_settings` | 查询收费标准 | "水费多少钱一吨？" | 物业费/水费/电费/燃气费单价 |
| 3 | `query_fee_history` | 查询缴费历史 | "我之前交了多少钱？" | 查看已缴和未缴的所有账单 |
| 4 | `create_repair` | 创建报修 | "我家水管漏水了" | 自动创建报修工单 |
| 5 | `query_repair_status` | 查询报修进度 | "我的报修处理了吗？" | 显示最近5条报修记录 |
| 6 | `cancel_repair` | 撤回报修 | "我不想报修了，撤回吧" | 仅待审核状态可撤回 |
| 7 | `query_notices` | 查询通知 | "最近有什么通知？" | 最新5条通知 |
| 8 | `query_owner_info` | 查询业主信息 | "我的房号是多少？" | 姓名/电话/楼栋/单元/房间号 |
| 9 | `evaluate_repair` | 评价维修 | "给维修打个5分" | 不指定工单ID自动评价最近已完成工单 |
| 10 | `query_activities` | 查询活动 | "最近有什么活动？" | 活动标题/时间/地点 |
| 11 | `signup_activity` | 报名活动 | "我要报名周末的活动" | 报名参加社区活动 |
| 12 | `cancel_activity_signup` | 取消报名 | "我不去了，取消报名吧" | 取消已报名的活动 |
| 13 | `query_my_activities` | 查我的活动 | "我报名了哪些活动？" | 查看已报名的活动列表 |
| 14 | `submit_feedback` | 提交反馈 | "我要投诉楼道灯不亮" | 提交意见反馈 |
| 15 | `create_forum_post` | 论坛发帖 | "帮我发个帖子" | 不指定分区时自动判断 |
| 16 | `search_forum_posts` | 搜索帖子 | "搜一下停水的帖子" | 模糊搜索标题和内容 |
| 17 | `query_my_forum_posts` | 查我的帖子 | "看看我发过的帖子" | 查询自己发布的帖子 |
| 18 | `query_notifications` | 查询通知 | "我有什么新消息？" | 查询推送通知 |
| 19 | `mark_notification_read` | 标记已读 | "把通知都标为已读" | 标记通知为已读 |

### 管理员专用工具（12个）

| # | 工具名 | 功能 | 示例触发语 | 说明 |
|---|--------|------|-----------|------|
| 20 | `dispatch_repair` | 派单 | "把工单5派给张师傅" | 仅待审核工单可派单 |
| 21 | `query_available_workers` | 查询维修员 | "有哪些维修员空闲？" | 列出状态正常的维修员 |
| 22 | `query_repair_list` | 查报修列表 | "看看所有报修工单" | 可按状态筛选 |
| 23 | `query_worker_stats` | 查维修员绩效 | "张师傅干得怎么样？" | 完成数/评分/处理中工单 |
| 24 | `query_dashboard_stats` | 系统概览 | "帮我看看系统概况" | 业主数/报修统计/缴费率等 |
| 25 | `query_fee_list` | 查账单列表 | "看看所有账单" | 可按已缴/未缴筛选 |
| 26 | `create_fee` | 创建账单 | "给3号楼101业主加个水费" | 为指定业主创建单条账单 |
| 27 | `batch_create_fee` | 批量生成账单 | "生成5月份的物业费" | 为所有业主批量生成 |
| 28 | `urge_payment` | 一键催缴 | "催一下没交费的业主" | 批量标记催缴提醒 |
| 29 | `publish_notice` | 一键发通知 | "发个通知：明天停水" | 创建通知公告，可设普通/紧急 |
| 30 | `query_feedback_list` | 查反馈列表 | "看看业主有什么反馈" | 可按待处理/已处理筛选 |
| 31 | `process_feedback` | 处理反馈 | "反馈3已处理，回复已维修" | 标记已处理并可添加回复 |

---

## 四、典型对话场景

### 场景1：业主报修
```
业主: 我家厨房水管漏水了，很急！
Agent: [调用 create_repair]
       已为您创建报修单，单号#15，状态：待审核。
       管理员将尽快派维修员处理，请耐心等待。
```

### 场景2：业主撤回报修
```
业主: 水管我自己修好了，把报修撤回吧
Agent: [调用 cancel_repair(repair_id=15, owner_id=当前用户)]
       报修工单#15已撤回成功。
```

### 场景3：业主缴费查询
```
业主: 我之前交了多少钱？
Agent: [调用 query_fee_history]
       您的缴费记录：
       - 已缴：1200.00元（5笔）
       - 未缴：350.00元（2笔）
       未缴明细：
       - 2026-04 水费: 150.00元
       - 2026-04 电费: 200.00元
```

### 场景4：管理员派单
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

### 场景5：管理员批量生成账单
```
管理员: 生成5月份的物业费，每户200元
Agent: [调用 batch_create_fee(type="物业费", month="2026-05", amount=200)]
       批量生成完成！
       - 类型：物业费
       - 月份：2026-05
       - 金额：200.00元/户
       - 已生成：45户
       - 跳过（已存在）：3户
```

### 场景6：管理员处理反馈
```
管理员: 有什么新反馈？
Agent: [调用 query_feedback_list(status=0)]
       有2条待处理反馈：
       - #3 李先生：楼道灯不亮
       - #5 王女士：电梯异响

管理员: 反馈3已处理，灯泡换了
Agent: [调用 process_feedback(feedback_id=3, reply="灯泡已更换")]
       反馈处理成功！已标记为已处理，已回复业主。
```

### 场景7：管理员查维修员绩效
```
管理员: 张师傅干得怎么样？
Agent: [调用 query_worker_stats(worker_id=3)]
       张师傅绩效统计：
       - 状态：正常
       - 已完成：25单
       - 处理中：2单
       - 评价数：20条
       - 平均评分：4.5分
```

### 场景8：业主取消活动报名
```
业主: 周末有事去不了，取消活动报名
Agent: [调用 cancel_activity_signup]
       已取消报名"端午节包粽子活动"，当前还有28人报名。
```

---

## 五、权限控制

Agent根据JWT Token中的用户角色自动控制可用工具：

| 角色 | role值 | 可用工具数 | 说明 |
|------|--------|-----------|------|
| 管理员 | 0 | 31个（全部） | 业主工具 + 管理员工具 |
| 业主 | 1 | 19个 | 业主工具 |
| 维修员 | 2 | 0个 | 维修员不使用Agent |

> system prompt 中标注用户角色，LLM 根据角色信息决定使用哪些工具。业主尝试调用管理员工具时，Agent 会回复权限不足。

---

## 六、技术架构

```
用户消息
   │
   ▼
AgentController (/agent/chat)
   │ 提取 userId、role (from JWT)
   ▼
PropertyAgent.chat(userId, role, message, history)
   │
   ├─ 构建系统提示词（含用户ID、角色、工具说明）
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
| PropertyAgentTools.java | backend/.../agent | 31个工具定义与执行 |
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
