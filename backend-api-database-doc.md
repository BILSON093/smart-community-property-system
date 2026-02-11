# 后端接口与数据库结构文档

## 1. 项目概述

本项目是一个基于Spring Boot的智慧社区后端系统，提供了完整的社区管理功能，包括用户管理、报修管理、费用管理、通知管理、留言反馈、论坛管理、活动管理等模块。系统采用分层架构，使用MyBatis-Plus作为ORM框架，MySQL作为数据库。

## 2. 后端接口详细说明

### 2.1 用户管理接口 (`/user`)

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应 |
|---------|------|---------|--------|------|
| `/user/login` | POST | 用户登录 | `{"username": "用户名", "password": "密码"}` | 登录成功返回token和用户信息 |
| `/user/register/worker` | POST | 维修员注册 | `{"username": "用户名", "password": "密码", "realName": "真实姓名", "phone": "手机号"}` | 注册结果 |
| `/user/register/owner` | POST | 业主注册 | `{"username": "用户名", "password": "密码", "realName": "真实姓名", "phone": "手机号", "building": "楼栋", "unit": "单元", "room": "房间", "idCard": "身份证号"}` | 注册结果 |
| `/user/info` | GET | 获取当前用户信息 | N/A | 用户详细信息 |
| `/user/register` | POST | 业主注册（兼容旧路径） | 同业主注册 | 注册结果 |
| `/user/update` | PUT | 更新用户信息 | 同业主注册，需包含userId | 更新结果 |
| `/user/change-password` | POST | 修改密码 | `{"oldPassword": "旧密码", "newPassword": "新密码"}` | 修改结果 |

### 2.2 管理员接口 (`/admin`)

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应 |
|---------|------|---------|--------|------|
| `/admin/owners` | GET | 获取所有业主列表 | N/A | 业主列表 |
| `/admin/workers` | GET | 获取所有维修员列表 | N/A | 维修员列表 |
| `/admin/owner/add` | POST | 新增业主 | 同业主注册 | 操作结果 |
| `/admin/owner/update` | PUT | 编辑业主 | 同业主注册，需包含userId | 操作结果 |
| `/admin/owner/{id}` | DELETE | 删除业主 | N/A | 操作结果 |
| `/admin/worker/add` | POST | 新增维修员 | 同维修员注册 | 操作结果 |
| `/admin/worker/update` | PUT | 编辑维修员 | 同维修员注册，需包含userId | 操作结果 |
| `/admin/worker/approve` | PUT | 审核维修员 | `workerId`和`status`参数 | 操作结果 |
| `/admin/worker/{id}` | DELETE | 删除维修员 | N/A | 操作结果 |
| `/admin/statistics` | GET | 获取统计数据 | N/A | 统计信息 |
| `/admin/list` | GET | 获取所有管理员列表 | N/A | 管理员列表 |
| `/admin/add` | POST | 添加管理员 | 同用户注册 | 操作结果 |
| `/admin/update` | PUT | 更新管理员信息 | 同用户注册，需包含userId | 操作结果 |
| `/admin/delete/{id}` | DELETE | 删除管理员 | N/A | 操作结果 |

### 2.3 报修管理接口 (`/repair`)

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应 |
|---------|------|---------|--------|------|
| `/repair/list` | GET | 报修列表（管理员） | 支持分页和状态筛选 | 报修列表 |
| `/repair/my` | GET | 我的报修（业主） | 支持分页 | 报修列表 |
| `/repair/worker` | GET | 我的工单（维修员） | 支持分页 | 报修列表 |
| `/repair/worker/completed` | GET | 我的已完成订单（维修员） | 支持分页 | 报修列表 |
| `/repair/{id}` | GET | 报修详情 | N/A | 报修详情 |
| `/repair/add` | POST | 提交报修 | `{"content": "报修内容", "images": ["图片URL"]}` | 操作结果 |
| `/repair/dispatch` | POST | 派单 | `{"repairId": 报修ID, "workerId": 维修员ID}` | 操作结果 |
| `/repair/start/{repairId}` | POST | 开始维修 | N/A | 操作结果 |
| `/repair/complete/{repairId}` | POST | 完成维修 | N/A | 操作结果 |
| `/repair/{id}` | DELETE | 删除报修 | N/A | 操作结果 |
| `/repair/evaluate` | POST | 评价维修服务 | `{"repairId": 报修ID, "score": 评分, "comment": "评价内容"}` | 操作结果 |
| `/repair/cancel/{repairId}` | POST | 退单（维修员取消维修） | N/A | 操作结果 |

### 2.4 费用管理接口 (`/fee`)

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应 |
|---------|------|---------|--------|------|
| `/fee/list` | GET | 缴费列表（管理员） | 支持分页、月份、状态、类型、关键词筛选 | 费用列表 |
| `/fee/my` | GET | 我的缴费列表（业主） | 支持分页 | 费用列表 |
| `/fee/add` | POST | 添加缴费记录 | `{"ownerId": 业主ID, "type": "费用类型", "month": "月份", "amount": 金额}` | 操作结果 |
| `/fee/pay/{id}` | POST | 支付 | N/A | 操作结果 |
| `/fee/{id}` | DELETE | 删除缴费记录 | N/A | 操作结果 |
| `/fee/settings` | GET | 获取费用单价设置 | N/A | 费用设置 |
| `/fee/settings` | POST | 保存费用单价设置 | `{"propertyFee": 物业费, "waterFee": 水费, "electricityFee": 电费, "gasFee": 燃气费}` | 操作结果 |
| `/fee/urge-payment` | POST | 一键催缴 | N/A | 操作结果 |

### 2.5 通知管理接口 (`/notice`)

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应 |
|---------|------|---------|--------|------|
| `/notice/list` | GET | 通知列表 | 支持分页 | 通知列表 |
| `/notice/{id}` | GET | 通知详情 | N/A | 通知详情 |
| `/notice/add` | POST | 添加通知 | `{"title": "标题", "content": "内容", "type": 类型, "image": "图片URL", "attachment": "附件URL"}` | 操作结果 |
| `/notice/update` | PUT | 更新通知 | 同添加通知，需包含id | 操作结果 |
| `/notice/{id}` | DELETE | 删除通知 | N/A | 操作结果 |

### 2.6 留言反馈接口 (`/feedback`)

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应 |
|---------|------|---------|--------|------|
| `/feedback/list` | GET | 留言列表（管理员） | 支持分页和状态筛选 | 留言列表 |
| `/feedback/my` | GET | 我的留言列表（业主） | `userId`参数 | 留言列表 |
| `/feedback/{id}` | GET | 留言详情 | N/A | 留言详情 |
| `/feedback/add` | POST | 添加留言 | `{"content": "留言内容", "userId": "用户ID", "userName": "用户名"}` | 操作结果 |
| `/feedback/update` | PUT | 更新留言（回复） | `{"id": 留言ID, "status": 状态, "reply": "回复内容"}` | 操作结果 |
| `/feedback/{id}` | DELETE | 删除留言 | N/A | 操作结果 |

### 2.7 论坛管理接口 (`/forum`)

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应 |
|---------|------|---------|--------|------|
| `/forum/list` | GET | 论坛列表 | 支持分页、分类筛选和关键词搜索 | 论坛帖子列表 |
| `/forum/list/user` | GET | 论坛列表（用户视角） | 支持分页 | 论坛帖子列表 |
| `/forum/{id}` | GET | 帖子详情 | N/A | 帖子详情 |
| `/forum/add` | POST | 发布帖子 | `{"title": "标题", "content": "内容", "categoryId": 分类ID, "images": ["图片URL"]}` | 操作结果 |
| `/forum/{id}` | DELETE | 删除帖子 | N/A | 操作结果 |
| `/forum/update` | PUT | 更新帖子 | 同发布帖子，需包含id | 操作结果 |
| `/forum/{id}/pin` | PUT | 置顶帖子 | N/A | 操作结果 |
| `/forum/{id}/unpin` | PUT | 取消置顶帖子 | N/A | 操作结果 |
| `/forum/my-posts` | GET | 我的帖子 | N/A | 帖子列表 |
| `/forum/category/list` | GET | 分类列表 | N/A | 分类列表 |
| `/forum/category/list/page` | GET | 分类分页列表 | 支持分页 | 分类列表 |
| `/forum/category/{id}` | GET | 分类详情 | N/A | 分类详情 |
| `/forum/category/add` | POST | 添加分类 | `{"name": "分类名称", "description": "描述", "sortOrder": 排序}` | 操作结果 |
| `/forum/category/update` | PUT | 更新分类 | 同添加分类，需包含id | 操作结果 |
| `/forum/category/{id}` | DELETE | 删除分类 | N/A | 操作结果 |
| `/forum/{forumId}/comments` | GET | 获取评论 | 支持分页 | 评论列表 |
| `/forum/{forumId}/comments` | POST | 添加评论 | `{"content": "评论内容", "images": ["图片URL"]}` | 操作结果 |
| `/forum/comments/{id}` | DELETE | 删除评论 | N/A | 操作结果 |
| `/forum/{forumId}/like` | POST | 点赞/取消点赞 | N/A | 操作结果 |
| `/forum/{forumId}/like/check` | GET | 检查是否点赞 | N/A | 是否点赞 |

### 2.8 活动管理接口 (`/activity`)

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应 |
|---------|------|---------|--------|------|
| `/activity/list` | GET | 活动列表 | 支持分页 | 活动列表 |
| `/activity/{id}` | GET | 活动详情 | N/A | 活动详情 |
| `/activity/add` | POST | 添加活动 | `{"title": "标题", "coverImage": "封面图", "description": "描述", "startTime": "开始时间", "endTime": "结束时间", "location": "地点", "images": "图片列表"}` | 操作结果 |
| `/activity/update` | PUT | 更新活动 | 同添加活动，需包含id | 操作结果 |
| `/activity/{id}` | DELETE | 删除活动 | N/A | 操作结果 |

### 2.9 轮播图管理接口 (`/carousel`)

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应 |
|---------|------|---------|--------|------|
| `/carousel/list` | GET | 轮播图列表 | N/A | 轮播图列表 |
| `/carousel/add` | POST | 添加轮播图 | `{"imageUrl": "图片URL", "sortOrder": 排序, "isShow": 是否显示}` | 操作结果 |
| `/carousel/update` | PUT | 更新轮播图 | 同添加轮播图，需包含id | 操作结果 |
| `/carousel/{id}` | DELETE | 删除轮播图 | N/A | 操作结果 |

### 2.10 AI客服接口 (`/ai`)

| 接口路径 | 方法 | 功能描述 | 请求体 | 响应 |
|---------|------|---------|--------|------|
| `/ai/chat` | POST | AI客服对话 | `{"question": "问题"}` | AI回复 |

## 3. 数据库表结构详细说明

### 3.1 用户相关表

#### 3.1.1 `sys_user` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 用户ID |
| `username` | `varchar(50)` | `UNIQUE, NOT NULL` | 用户名 |
| `password` | `varchar(255)` | `NOT NULL` | 密码（加密存储） |
| `real_name` | `varchar(50)` | `NULL` | 真实姓名 |
| `phone` | `varchar(20)` | `NULL` | 手机号 |
| `avatar` | `varchar(255)` | `NULL` | 头像URL |
| `role` | `tinyint` | `NOT NULL, DEFAULT 0` | 角色（0：业主，1：维修员，2：管理员） |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |

#### 3.1.2 `sys_owner` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `user_id` | `bigint` | `PRIMARY KEY` | 用户ID（关联sys_user.id） |
| `building` | `varchar(20)` | `NULL` | 楼栋 |
| `unit` | `varchar(20)` | `NULL` | 单元 |
| `room` | `varchar(20)` | `NULL` | 房间号 |
| `id_card` | `varchar(18)` | `NULL, DEFAULT ''` | 身份证号 |

#### 3.1.3 `sys_repair_worker` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `user_id` | `bigint` | `PRIMARY KEY` | 用户ID（关联sys_user.id） |
| `status` | `tinyint` | `NOT NULL, DEFAULT 0` | 状态（0：待审核，1：审核通过，2：审核拒绝） |

### 3.2 报修相关表

#### 3.2.1 `bus_repair` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 报修ID |
| `owner_id` | `bigint` | `NOT NULL, FOREIGN KEY` | 业主ID（关联sys_user.id） |
| `content` | `text` | `NOT NULL` | 报修内容 |
| `images` | `json` | `NULL` | 图片URL列表 |
| `status` | `tinyint` | `NOT NULL, DEFAULT 0, INDEX` | 状态（0：待处理，1：已派单，2：维修中，3：已完成，4：已取消） |
| `worker_id` | `bigint` | `NULL, FOREIGN KEY` | 维修员ID（关联sys_user.id） |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `update_time` | `datetime` | `NULL, ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |
| `complete_time` | `datetime` | `NULL` | 完成时间 |

#### 3.2.2 `bus_evaluation` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 评价ID |
| `repair_id` | `bigint` | `NOT NULL, FOREIGN KEY` | 报修ID（关联bus_repair.id） |
| `score` | `tinyint` | `NOT NULL` | 评分（1-5分） |
| `comment` | `text` | `NULL` | 评价内容 |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |

### 3.3 费用相关表

#### 3.3.1 `bus_fee` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 费用ID |
| `owner_id` | `bigint` | `NOT NULL, FOREIGN KEY` | 业主ID（关联sys_user.id） |
| `type` | `varchar(50)` | `NOT NULL` | 费用类型（物业费、水费、电费、燃气费等） |
| `month` | `varchar(20)` | `NOT NULL, INDEX` | 月份（格式：YYYY-MM） |
| `amount` | `decimal(10,2)` | `NOT NULL` | 金额 |
| `status` | `tinyint` | `NOT NULL, DEFAULT 0` | 状态（0：未支付，1：已支付） |
| `pay_time` | `datetime` | `NULL` | 支付时间 |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `remark` | `varchar(255)` | `NULL` | 备注 |
| `reminded` | `tinyint` | `NOT NULL, DEFAULT 0` | 是否已催缴（0：未催缴，1：已催缴） |

#### 3.3.2 `bus_fee_settings` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 设置ID |
| `property_fee` | `decimal(10,2)` | `NOT NULL, DEFAULT 2.50` | 物业费单价（元/平方米） |
| `water_fee` | `decimal(10,2)` | `NOT NULL, DEFAULT 3.80` | 水费单价（元/吨） |
| `electricity_fee` | `decimal(10,2)` | `NOT NULL, DEFAULT 0.60` | 电费单价（元/度） |
| `gas_fee` | `decimal(10,2)` | `NOT NULL, DEFAULT 2.20` | 燃气费单价（元/立方米） |

### 3.4 通知相关表

#### 3.4.1 `bus_notice` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 通知ID |
| `title` | `varchar(200)` | `NOT NULL` | 标题 |
| `content` | `text` | `NOT NULL` | 内容 |
| `type` | `tinyint` | `NOT NULL, DEFAULT 0` | 类型（0：普通通知，1：重要通知） |
| `publish_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 发布时间 |
| `image` | `varchar(500)` | `NULL` | 图片URL |
| `attachment` | `varchar(500)` | `NULL` | 附件URL |

### 3.5 留言反馈相关表

#### 3.5.1 `bus_feedback` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 留言ID |
| `content` | `text` | `NOT NULL` | 留言内容 |
| `user_id` | `varchar(255)` | `NOT NULL` | 用户ID |
| `user_name` | `varchar(255)` | `NOT NULL` | 用户名 |
| `status` | `int` | `NULL, DEFAULT 0` | 状态（0：待处理，1：已处理） |
| `reply` | `text` | `NULL` | 回复内容 |
| `create_time` | `datetime` | `NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `update_time` | `datetime` | `NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 3.6 论坛相关表

#### 3.6.1 `bus_forum` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 帖子ID |
| `user_id` | `bigint` | `NOT NULL, FOREIGN KEY` | 用户ID（关联sys_user.id） |
| `category_id` | `bigint` | `NULL, FOREIGN KEY` | 分类ID（关联bus_forum_category.id） |
| `title` | `varchar(200)` | `NULL` | 标题 |
| `content` | `text` | `NOT NULL` | 内容 |
| `images` | `json` | `NULL` | 图片URL列表 |
| `is_public` | `tinyint` | `NOT NULL, DEFAULT 0, INDEX` | 是否公开（0：公开，1：私密） |
| `is_pinned` | `tinyint` | `NOT NULL, DEFAULT 0, INDEX` | 是否置顶（0：未置顶，1：已置顶） |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |

#### 3.6.2 `bus_forum_category` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 分类ID |
| `name` | `varchar(50)` | `NOT NULL` | 分类名称 |
| `description` | `varchar(200)` | `NULL` | 分类描述 |
| `sort_order` | `int` | `NOT NULL, DEFAULT 0` | 排序顺序 |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |

#### 3.6.3 `bus_forum_comment` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 评论ID |
| `forum_id` | `bigint` | `NOT NULL, FOREIGN KEY` | 帖子ID（关联bus_forum.id） |
| `user_id` | `bigint` | `NOT NULL, FOREIGN KEY` | 用户ID（关联sys_user.id） |
| `content` | `text` | `NOT NULL` | 评论内容 |
| `images` | `json` | `NULL` | 图片URL列表 |
| `is_public` | `int` | `NULL, DEFAULT 1` | 是否公开（0：不公开，1：公开） |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |

#### 3.6.4 `bus_forum_like` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 点赞ID |
| `forum_id` | `bigint` | `NOT NULL, FOREIGN KEY` | 帖子ID（关联bus_forum.id） |
| `user_id` | `bigint` | `NOT NULL, FOREIGN KEY` | 用户ID（关联sys_user.id） |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |

### 3.7 活动相关表

#### 3.7.1 `bus_activity` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 活动ID |
| `title` | `varchar(200)` | `NOT NULL` | 活动标题 |
| `cover_image` | `varchar(255)` | `NULL` | 封面图片URL |
| `description` | `text` | `NULL` | 活动描述 |
| `start_time` | `datetime` | `NULL` | 开始时间 |
| `end_time` | `datetime` | `NULL` | 结束时间 |
| `location` | `varchar(200)` | `NULL` | 活动地点 |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `images` | `text` | `NULL` | 活动图片列表 |

### 3.8 轮播图相关表

#### 3.8.1 `bus_carousel` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 轮播图ID |
| `image_url` | `varchar(255)` | `NOT NULL` | 图片URL |
| `sort_order` | `int` | `NOT NULL, DEFAULT 0` | 排序顺序 |
| `is_show` | `tinyint` | `NOT NULL, DEFAULT 1` | 是否显示（0：隐藏，1：显示） |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | 创建时间 |

### 3.9 聊天相关表

#### 3.9.1 `chat_record` 表

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | `bigint` | `PRIMARY KEY, AUTO_INCREMENT` | 聊天记录ID |
| `session_id` | `bigint` | `NOT NULL, INDEX` | 会话ID |
| `sender` | `tinyint` | `NOT NULL` | 发送者（0：用户，1：AI） |
| `content` | `text` | `NOT NULL` | 消息内容 |
| `msg_type` | `varchar(20)` | `NOT NULL, DEFAULT 'text'` | 消息类型（text：文本，image：图片等） |
| `create_time` | `datetime` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX` | 创建时间 |

## 4. 总结

本后端系统提供了完整的社区管理功能，涵盖了用户管理、报修管理、费用管理、通知管理、留言反馈、论坛管理、活动管理、轮播图管理和AI客服等多个模块。系统设计合理，接口完整，数据库表结构清晰，能够满足智慧社区的日常管理需求。

系统采用了Spring Boot框架，结合MyBatis-Plus作为ORM工具，使用MySQL作为数据库，具有良好的可扩展性和维护性。通过JWT实现了用户认证，保证了接口的安全性。

本文档详细描述了系统的所有接口和数据库表结构，为前端开发和系统维护提供了完整的参考资料。