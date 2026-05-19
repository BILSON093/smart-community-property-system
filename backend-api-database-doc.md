# 后端接口与数据库结构文档

## 1. 项目概述

基于 Spring Boot 2.7 的智慧社区后端系统，提供用户管理、报修管理、费用管理、通知管理、论坛管理、活动管理、AI Agent 等完整功能。采用 MyBatis Plus 作为 ORM，MySQL 8.0 作为数据库。

## 2. 后端接口

### 2.1 用户管理 `/user`

| 接口 | 方法 | 功能 | 请求体 |
|------|------|------|--------|
| `/user/login` | POST | 登录 | `{username, password, loginType}` (loginType: 0=管理员, 1=业主, 2=维修员) |
| `/user/register/owner` | POST | 业主注册 | `{username, password, realName, phone, building, unit, room, idCard}` |
| `/user/register/worker` | POST | 维修员注册 | `{username, password, realName, phone}` |
| `/user/register/admin` | POST | 管理员注册 | `{username, password, realName, phone}` |
| `/user/register` | POST | 业主注册(兼容) | 同业主注册 |
| `/user/info` | GET | 当前用户信息 | - |
| `/user/update` | PUT | 更新用户信息 | 同注册 + userId |
| `/user/change-password` | POST | 修改密码 | `{oldPassword, newPassword}` |

### 2.2 管理员 `/admin`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/admin/owners` | GET | 业主列表 | - |
| `/admin/workers` | GET | 维修员列表 | - |
| `/admin/owner/add` | POST | 新增业主 | 同注册 |
| `/admin/owner/update` | PUT | 编辑业主 | 同注册 |
| `/admin/owner/{id}` | DELETE | 删除业主 | - |
| `/admin/worker/add` | POST | 新增维修员 | 同注册 |
| `/admin/worker/update` | PUT | 编辑维修员 | 同注册 |
| `/admin/worker/approve` | PUT | 审核维修员 | `{workerId, status}` (0=待审, 1=通过, 2=拒绝) |
| `/admin/worker/{id}` | DELETE | 删除维修员 | - |
| `/admin/statistics` | GET | 统计数据 | 返回 ownerCount, pendingRepairs, monthFee, todayActive, workerCount, avgWorkerScore, repairTypes, feeRate, weekFeeTrend, weekForumTrend |
| `/admin/list` | GET | 管理员列表 | - |
| `/admin/add` | POST | 添加管理员 | 同注册 |
| `/admin/update` | PUT | 更新管理员 | 同注册 |
| `/admin/delete/{id}` | DELETE | 删除管理员 | - |

### 2.3 报修管理 `/repair`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/repair/list` | GET | 报修列表(管理员) | 支持分页、status、type筛选 |
| `/repair/my` | GET | 我的报修(业主) | 按owner_id筛选 |
| `/repair/worker` | GET | 我的工单(维修员) | 进行中的工单 |
| `/repair/worker/completed` | GET | 已完成工单(维修员) | 已完成的工单 |
| `/repair/worker/stats` | GET | 维修员绩效统计 | 返回 completedCount, avgScore, avgMinutes |
| `/repair/{id}` | GET | 报修详情 | - |
| `/repair/add` | POST | 提交报修 | `{content, images[], type}` |
| `/repair/dispatch` | POST | 派单 | `{repairId, workerId}` |
| `/repair/start/{id}` | POST | 开始维修 | - |
| `/repair/complete/{id}` | POST | 完成维修 | - |
| `/repair/cancel/{id}` | POST | 退单 | - |
| `/repair/{id}` | DELETE | 删除报修 | - |
| `/repair/evaluate` | POST | 评价维修 | `{repairId, score, comment}` |

### 2.4 费用管理 `/fee`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/fee/list` | GET | 缴费列表(管理员) | 支持分页、month、status、type、keyword筛选 |
| `/fee/my` | GET | 我的缴费(业主) | - |
| `/fee/add` | POST | 添加缴费 | `{ownerId, type, month, amount, usage, remark, status}` |
| `/fee/pay/{id}` | POST | 缴费支付 | 检查是否已缴费 |
| `/fee/{id}` | DELETE | 删除缴费 | - |
| `/fee/settings` | GET | 获取费用单价 | - |
| `/fee/settings` | POST | 保存费用单价 | `{propertyFee, waterFee, electricityFee, gasFee}` |
| `/fee/urge-payment` | POST | 一键催缴 | 标记催缴 + 创建通知 |

### 2.5 通知公告 `/notice`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/notice/list` | GET | 通知列表 | 支持分页 |
| `/notice/{id}` | GET | 通知详情 | - |
| `/notice/add` | POST | 添加通知 | `{title, content, type, image, attachment}` |
| `/notice/update` | PUT | 更新通知 | 同添加 + id |
| `/notice/{id}` | DELETE | 删除通知 | - |
| `/notice/{id}/read` | POST | 标记公告已读 | 业主调用 |
| `/notice/{id}/read-status` | GET | 已读状态 | - |
| `/notice/{id}/read-count` | GET | 已读人数 | 管理员调用 |

### 2.6 推送通知 `/notification`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/notification/my` | GET | 我的通知 | 分页，按时间倒序 |
| `/notification/read/{id}` | POST | 标记已读 | - |
| `/notification/read-all` | POST | 全部已读 | - |
| `/notification/unread-count` | GET | 未读数 | - |
| `/notification/list` | GET | 所有通知(管理员) | 分页 |

### 2.7 留言反馈 `/feedback`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/feedback/list` | GET | 留言列表(管理员) | 支持分页、status筛选 |
| `/feedback/my` | GET | 我的留言(业主) | 按userId筛选 |
| `/feedback/{id}` | GET | 留言详情 | - |
| `/feedback/add` | POST | 添加留言 | `{content}` |
| `/feedback/update` | PUT | 更新/回复 | `{id, status, reply}` |
| `/feedback/{id}` | DELETE | 删除留言 | - |

### 2.8 论坛 `/forum`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/forum/list` | GET | 论坛列表 | 支持分页、分类、搜索，默认只显示已审核(status=1) |
| `/forum/list/all` | GET | 全部帖子(管理员) | 含所有审核状态 |
| `/forum/{id}` | GET | 帖子详情 | - |
| `/forum/add` | POST | 发帖 | `{title, content, categoryId, images[]}` 默认status=0(待审) |
| `/forum/update` | PUT | 更新帖子 | - |
| `/forum/{id}` | DELETE | 删除帖子 | - |
| `/forum/{id}/pin` | PUT | 置顶 | - |
| `/forum/{id}/unpin` | PUT | 取消置顶 | - |
| `/forum/{id}/approve` | PUT | 审核通过 | 管理员 |
| `/forum/{id}/reject` | PUT | 审核拒绝 | 管理员 |
| `/forum/my-posts` | GET | 我的帖子 | - |
| `/forum/category/list` | GET | 分类列表 | - |
| `/forum/category/add` | POST | 添加分类 | `{name, description, sortOrder}` |
| `/forum/category/update` | PUT | 更新分类 | - |
| `/forum/category/{id}` | DELETE | 删除分类 | - |
| `/forum/{id}/comments` | GET | 评论列表 | - |
| `/forum/{id}/comments` | POST | 添加评论 | `{content, images[]}` |
| `/forum/comments/{id}` | DELETE | 删除评论 | - |
| `/forum/{id}/like` | POST | 点赞/取消 | - |
| `/forum/{id}/like/check` | GET | 检查点赞 | - |

### 2.9 活动 `/activity`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/activity/list` | GET | 活动列表 | 支持分页 |
| `/activity/{id}` | GET | 活动详情 | - |
| `/activity/add` | POST | 添加活动 | `{title, coverImage, description, startTime, endTime, location, images}` |
| `/activity/update` | PUT | 更新活动 | - |
| `/activity/{id}` | DELETE | 删除活动 | - |
| `/activity/{id}/signup` | POST | 报名活动 | 业主调用 |
| `/activity/{id}/signup` | DELETE | 取消报名 | 业主调用 |
| `/activity/{id}/signups` | GET | 报名列表 | 管理员调用 |
| `/activity/{id}/signup-status` | GET | 报名状态 | 返回 isSignedUp, signupCount |

### 2.10 轮播图 `/carousel`

| 接口 | 方法 | 功能 |
|------|------|------|
| `/carousel/list` | GET | 轮播图列表 |
| `/carousel/add` | POST | 添加 |
| `/carousel/update` | PUT | 更新 |
| `/carousel/{id}` | DELETE | 删除 |

### 2.11 AI客服 `/ai`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/ai/chat` | POST | AI客服对话 | `{question, imageUrl}` RAG模式 |
| `/ai/config/list` | GET | AI配置列表 | 管理员 |
| `/ai/config/save` | POST | 保存AI配置 | 管理员 |
| `/ai/config/delete/{id}` | DELETE | 删除AI配置 | 管理员 |
| `/ai/config/test` | POST | 测试AI配置 | 管理员 |

### 2.12 AI Agent `/agent`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/agent/chat` | POST | Agent对话 | `{message, history[]}` Function Calling模式，17个工具 |

### 2.13 在线客服 `/chat`

| 接口 | 方法 | 功能 | 说明 |
|------|------|------|------|
| `/chat/send` | POST | 发送消息 | `{sender, content, msgType}` 业主 |
| `/chat/session` | GET | 会话记录 | 业主 |
| `/chat/admin/info` | GET | 管理员信息 | 业主/管理员 |
| `/chat/admin/sessions` | GET | 所有会话列表 | 管理员 |
| `/chat/admin/reply` | POST | 回复消息 | `{sessionId, content, msgType}` 管理员 |
| `/chat/admin/session?sessionId=` | GET | 指定会话消息 | 管理员 |

### 2.14 文件上传 `/common`

| 接口 | 方法 | 功能 |
|------|------|------|
| `/common/upload` | POST | 文件上传（返回 `{url, name}`） |

---

## 3. 数据库表结构

### 3.1 用户相关

#### `sys_user`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 用户ID |
| username | varchar(50) | UNIQUE, NOT NULL | 用户名 |
| password | varchar(255) | NOT NULL | 密码(BCrypt加密) |
| real_name | varchar(50) | NULL | 真实姓名 |
| phone | varchar(20) | NULL | 手机号 |
| avatar | varchar(255) | NULL | 头像URL |
| role | tinyint | NOT NULL, DEFAULT 0 | 角色(0=管理员, 1=业主, 2=维修员) |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### `sys_owner`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| user_id | bigint | PK | 用户ID(FK→sys_user.id) |
| building | varchar(20) | NULL | 楼栋 |
| unit | varchar(20) | NULL | 单元 |
| room | varchar(20) | NULL | 房间号 |
| id_card | varchar(18) | DEFAULT '' | 身份证号 |

#### `sys_repair_worker`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| user_id | bigint | PK | 用户ID(FK→sys_user.id) |
| status | tinyint | NOT NULL, DEFAULT 0 | 状态(0=待审核, 1=通过, 2=拒绝) |

#### `sys_notification`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 通知ID |
| user_id | bigint | NOT NULL | 接收用户ID |
| title | varchar(200) | NULL | 标题 |
| content | text | NULL | 内容 |
| type | varchar(50) | NULL | 类型 |
| is_read | int | DEFAULT 0 | 已读(0=未读, 1=已读) |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 3.2 报修相关

#### `bus_repair`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 报修ID |
| owner_id | bigint | NOT NULL | 业主ID(FK→sys_user.id) |
| content | text | NOT NULL | 报修内容 |
| type | varchar(50) | DEFAULT '其他' | 分类(水电维修/家电维修/管道疏通/门窗维修/其他) |
| images | json | NULL | 图片URL列表 |
| status | tinyint | NOT NULL, DEFAULT 0 | 状态(0=待处理, 1=已派单, 2=维修中, 3=已完成, 4=已取消) |
| worker_id | bigint | NULL | 维修员ID(FK→sys_user.id) |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | 更新时间 |
| complete_time | datetime | NULL | 完成时间 |

#### `bus_evaluation`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 评价ID |
| repair_id | bigint | NOT NULL | 报修ID(FK→bus_repair.id) |
| score | tinyint | NOT NULL | 评分(1-5) |
| comment | text | NULL | 评价内容 |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 3.3 费用相关

#### `bus_fee`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 费用ID |
| owner_id | bigint | NOT NULL | 业主ID(FK→sys_user.id) |
| type | varchar(50) | NOT NULL | 类型(物业费/水费/电费/燃气费/其他) |
| month | varchar(20) | NOT NULL | 月份(YYYY-MM) |
| amount | decimal(10,2) | NOT NULL | 金额 |
| status | tinyint | NOT NULL, DEFAULT 0 | 状态(0=未缴, 1=已缴) |
| pay_time | datetime | NULL | 支付时间 |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| remark | varchar(255) | NULL | 备注 |
| reminded | tinyint | DEFAULT 0 | 已催缴(0=否, 1=是) |

#### `bus_fee_settings`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 设置ID |
| property_fee | decimal(10,2) | DEFAULT 2.50 | 物业费(元/㎡) |
| water_fee | decimal(10,2) | DEFAULT 3.80 | 水费(元/吨) |
| electricity_fee | decimal(10,2) | DEFAULT 0.60 | 电费(元/度) |
| gas_fee | decimal(10,2) | DEFAULT 2.20 | 燃气费(元/m³) |

### 3.4 通知相关

#### `bus_notice`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 通知ID |
| title | varchar(200) | NOT NULL | 标题 |
| content | text | NOT NULL | 内容 |
| type | tinyint | DEFAULT 0 | 类型(0=普通, 1=重要) |
| publish_time | datetime | DEFAULT CURRENT_TIMESTAMP | 发布时间 |
| image | varchar(500) | NULL | 图片URL |
| attachment | varchar(500) | NULL | 附件URL |

#### `bus_notice_read`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | ID |
| notice_id | bigint | NOT NULL | 公告ID(FK→bus_notice.id) |
| user_id | bigint | NOT NULL | 用户ID(FK→sys_user.id) |
| read_time | datetime | DEFAULT CURRENT_TIMESTAMP | 阅读时间 |
| | | UNIQUE(notice_id, user_id) | |

### 3.5 活动相关

#### `bus_activity`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 活动ID |
| title | varchar(200) | NOT NULL | 标题 |
| cover_image | varchar(255) | NULL | 封面图 |
| description | text | NULL | 描述 |
| start_time | datetime | NULL | 开始时间 |
| end_time | datetime | NULL | 结束时间 |
| location | varchar(200) | NULL | 地点 |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| images | text | NULL | 图片列表 |

#### `bus_activity_signup`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | ID |
| activity_id | bigint | NOT NULL | 活动ID(FK→bus_activity.id) |
| user_id | bigint | NOT NULL | 用户ID(FK→sys_user.id) |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 报名时间 |
| | | UNIQUE(activity_id, user_id) | |

### 3.6 论坛相关

#### `bus_forum`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 帖子ID |
| user_id | bigint | NOT NULL | 用户ID(FK→sys_user.id) |
| category_id | bigint | NULL | 分类ID(FK→bus_forum_category.id) |
| title | varchar(200) | NULL | 标题 |
| content | text | NOT NULL | 内容 |
| images | json | NULL | 图片列表 |
| status | int | DEFAULT 1 | 审核状态(0=待审, 1=通过, 2=拒绝) |
| is_public | tinyint | DEFAULT 0 | 是否公开(0=公开, 1=私密) |
| is_pinned | tinyint | DEFAULT 0 | 是否置顶(0=否, 1=是) |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### `bus_forum_category`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 分类ID |
| name | varchar(50) | NOT NULL | 分类名称 |
| description | varchar(200) | NULL | 描述 |
| sort_order | int | DEFAULT 0 | 排序 |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### `bus_forum_comment`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 评论ID |
| forum_id | bigint | NOT NULL | 帖子ID(FK→bus_forum.id) |
| user_id | bigint | NOT NULL | 用户ID(FK→sys_user.id) |
| content | text | NOT NULL | 内容 |
| images | json | NULL | 图片列表 |
| is_public | int | DEFAULT 1 | 是否公开 |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### `bus_forum_like`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 点赞ID |
| forum_id | bigint | NOT NULL | 帖子ID(FK→bus_forum.id) |
| user_id | bigint | NOT NULL | 用户ID(FK→sys_user.id) |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 3.7 其他

#### `bus_feedback`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 留言ID |
| content | text | NOT NULL | 内容 |
| user_id | varchar(255) | NOT NULL | 用户ID |
| user_name | varchar(255) | NOT NULL | 用户名 |
| status | int | DEFAULT 0 | 状态(0=待处理, 1=已处理) |
| reply | text | NULL | 回复 |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### `bus_carousel`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | ID |
| image_url | varchar(255) | NOT NULL | 图片URL |
| sort_order | int | DEFAULT 0 | 排序 |
| is_show | tinyint | DEFAULT 1 | 显示(0=隐藏, 1=显示) |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### `chat_record`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | ID |
| session_id | bigint | NOT NULL | 会话ID |
| sender | tinyint | NOT NULL | 发送者(1=用户, 2=管理员) |
| content | text | NOT NULL | 内容 |
| msg_type | varchar(20) | DEFAULT 'text' | 类型(text/image) |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### `ai_config`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | ID |
| api_url | varchar(500) | NULL | API地址 |
| model_name | varchar(100) | NULL | 模型名称 |
| api_key | varchar(500) | NULL | API密钥 |
| enabled | tinyint | DEFAULT 1 | 启用(0=否, 1=是) |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

---

## 4. 角色权限

通过 `@RequireRole` 注解 + AOP 切面实现：

| 角色 | role值 | 说明 |
|------|--------|------|
| 管理员 | 0 | 全部管理功能 |
| 业主 | 1 | 缴费、报修、论坛、客服等 |
| 维修员 | 2 | 工单处理 |

JWT Token 中包含 userId、username、role，拦截器解析后存入 request attribute。
