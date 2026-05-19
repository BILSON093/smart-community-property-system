# 智慧社区微信小程序

## 项目简介

基于微信小程序原生开发的智慧社区应用，包含**业主端**和**维修员端**两种角色，通过登录时选择的角色自动切换界面。提供报修管理、缴费管理、通知公告、活动报名、社区论坛、AI智能助手、留言反馈等功能。

## 项目结构

```
miniprogram-owner/
├── app.js                  # 小程序入口（globalData、登录管理）
├── app.json                # 页面注册、Tab栏配置、网络超时
├── app.wxss                # 全局样式
├── project.config.json     # 项目配置
├── sitemap.json            # 站点地图
├── pages/                  # 页面目录（21个页面）
│   ├── index/             # 首页（Tab）
│   ├── news/              # 通知公告列表
│   ├── news-detail/       # 通知详情（自动标记已读）
│   ├── activity/          # 社区活动列表
│   ├── activity-detail/   # 活动详情（报名/取消报名）
│   ├── repair/            # 提交报修（含分类选择）
│   ├── repair-detail/     # 报修详情
│   ├── repairer-home/     # 维修员工作台（Tab）
│   ├── repairer-detail/   # 维修员工单详情
│   ├── pay/               # 缴费管理
│   ├── forum/             # 社区论坛（Tab）
│   ├── forum-detail/      # 帖子详情
│   ├── forum-publish/     # 发布帖子
│   ├── feedback/          # 留言反馈
│   ├── chat/              # AI客服/智能助手/人工客服（Tab）
│   ├── profile/           # 个人中心（Tab）
│   ├── notification/      # 通知中心（推送通知）
│   ├── login/             # 登录（选择角色：业主/维修员）
│   ├── register/          # 注册
│   ├── edit-profile/      # 编辑资料
│   └── change-password/   # 修改密码
├── utils/
│   ├── request.js         # 网络请求封装（wx.request + token + formatImages）
│   ├── api.js             # API接口封装（全部后端接口）
│   └── util.js            # 工具函数
└── images/                # 图片资源
    ├── logo.png
    ├── avatar-default.png
    ├── ai-avatar.png
    └── tab/               # 底部导航图标
```

## 功能模块

### 业主端
| 模块 | 功能 |
|------|------|
| 首页 | 轮播图、快捷入口、最新通知、最新活动、未读通知提醒 |
| 报修 | 提交报修（含分类：水电维修/家电维修/管道疏通/门窗维修/其他）、查看进度、评价维修 |
| 缴费 | 查看账单、在线支付、缴费记录 |
| AI客服 | 智能助手（Agent模式，可执行操作）、AI客服（RAG问答）、人工客服 |
| 论坛 | 浏览帖子、发帖（需审核）、评论、点赞 |
| 活动 | 查看活动、报名/取消报名 |
| 通知 | 公告列表（未读标记）、通知中心（推送通知、标记已读） |
| 个人中心 | 查看信息（含楼栋/单元/房间）、编辑资料、修改密码、退出登录 |

### 维修员端
| 模块 | 功能 |
|------|------|
| 工作台 | 待处理/进行中/已完成工单、刷新工单、统计卡片 |
| 工单详情 | 查看详情、开始维修、完成维修、退单 |
| 个人中心 | 退出登录 |

## 底部Tab栏

| Tab | 页面 | 图标 |
|-----|------|------|
| 首页 | pages/index/index | tab/home.png |
| 论坛 | pages/forum/forum | tab/forum.png |
| AI客服 | pages/chat/chat | tab/chat.png |
| 我的 | pages/profile/profile | tab/profile.png |

> 维修员登录后自动跳转到 repairer-home 页面，不显示底部Tab栏。

## 聊天模式

小程序聊天页面支持三种模式切换：

| 模式 | 说明 | 接口 |
|------|------|------|
| 智能助手 | Agent模式，可执行报修、查费、评价等操作 | POST /agent/chat |
| AI客服 | RAG模式，基于上下文问答 | POST /ai/chat |
| 人工客服 | 与管理员实时聊天 | POST /chat/send + GET /chat/session |

## 角色说明

- **业主（role=1）**：登录后进入首页Tab栏，可使用全部业主功能
- **维修员（role=2）**：登录后跳转到维修工作台，只能查看和处理工单
- 通过 `app.js` 中的 `globalData.token` 和 `globalData.userInfo.role` 控制

## 配置说明

后端接口地址在 `app.js` 中配置：

```javascript
globalData: {
  baseURL: 'http://localhost:8080/api'  // 修改为实际后端地址
}
```

## 版本记录

### v1.1.0
- 新增维修员端（工作台、工单详情）
- 新增AI智能助手（Agent模式，Function Calling）
- 新增通知中心（推送通知）
- 新增活动报名功能
- 新增公告已读追踪
- 新增报修分类选择
- 新增修改密码页面
- 优化头像加载（从服务器获取最新头像）
- 清理调试日志

### v1.0.0
- 初始版本，完成业主端全部核心功能
