# 微信小程序配置和使用指南

## 前置要求

1. 安装 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 拥有 [微信小程序账号](https://mp.weixin.qq.com/)（开发阶段可使用测试号）

## 配置步骤

### 1. 导入项目

1. 打开微信开发者工具
2. 选择"导入项目"
3. 项目目录选择 `miniprogram-owner/`
4. AppID 开发阶段可使用测试号或 `touristappid`
5. 点击"导入"

### 2. 配置后端接口

打开 `app.js`，修改 `baseURL`：

```javascript
globalData: {
  userInfo: null,
  token: null,
  baseURL: 'http://localhost:8080/api'  // 修改为实际地址
}
```

### 3. 网络域名配置

**开发阶段：** 微信开发者工具 → 详情 → 本地设置 → 勾选"不校验合法域名"

**正式发布：** 微信公众平台 → 开发 → 开发管理 → 服务器域名 → 添加 `request合法域名`

### 4. 图片资源

`images/` 目录需要以下文件：

| 文件 | 说明 |
|------|------|
| `logo.png` | 应用Logo |
| `avatar-default.png` | 默认头像 |
| `ai-avatar.png` | AI客服头像 |
| `tab/home.png` | 首页图标 |
| `tab/home-active.png` | 首页图标(选中) |
| `tab/forum.png` | 论坛图标 |
| `tab/forum-active.png` | 论坛图标(选中) |
| `tab/chat.png` | AI客服图标 |
| `tab/chat-active.png` | AI客服图标(选中) |
| `tab/profile.png` | 我的图标 |
| `tab/profile-active.png` | 我的图标(选中) |

### 5. 后端服务

确保后端已启动（`cd backend && mvn spring-boot:run`），主要接口：

| 接口 | 说明 |
|------|------|
| POST /user/login | 登录（loginType: 1=业主, 2=维修员） |
| POST /user/register/owner | 业主注册 |
| GET /user/info | 获取用户信息 |
| POST /agent/chat | AI智能助手 |
| POST /ai/chat | AI客服 |
| GET /notice/list | 通知列表 |
| GET /activity/list | 活动列表 |
| GET /fee/my | 我的缴费 |
| GET /repair/my | 我的报修 |
| GET /forum/list | 论坛列表 |
| GET /notification/my | 我的通知 |
| GET /notification/unread-count | 未读通知数 |

## 运行

1. 点击"编译"按钮
2. 左侧模拟器查看效果
3. 调试工具查看日志

## 常见问题

### 网络请求失败
- 确认后端已启动
- 检查 `app.js` 中 `baseURL` 配置
- 开发工具勾选"不校验合法域名"

### 图片加载失败
- 检查 `images/` 目录是否包含所需图片
- 后端 `/upload/**` 静态资源映射是否正常

### 登录后无法访问页面
- 清除缓存重新登录
- 检查后端 token 有效期（默认24小时）

### 角色跳转异常
- 业主（role=1）跳转首页Tab
- 维修员（role=2）跳转工作台页面
- 确认登录接口返回的 role 值正确

## 功能测试清单

### 基础
- [ ] 业主登录/注册
- [ ] 维修员登录
- [ ] 退出登录
- [ ] 修改密码

### 首页
- [ ] 轮播图
- [ ] 快捷入口跳转
- [ ] 通知列表
- [ ] 活动列表
- [ ] 未读通知提醒

### 报修
- [ ] 提交报修（含分类选择）
- [ ] 查看报修列表
- [ ] 查看报修详情
- [ ] 评价维修服务

### 缴费
- [ ] 查看缴费列表
- [ ] 在线支付

### AI客服
- [ ] 智能助手模式（Agent）
- [ ] AI客服模式
- [ ] 人工客服模式
- [ ] 模式切换

### 论坛
- [ ] 浏览帖子
- [ ] 发布帖子（提示需审核）
- [ ] 评论/点赞

### 活动
- [ ] 查看活动详情
- [ ] 报名/取消报名

### 通知
- [ ] 公告列表（未读标记）
- [ ] 公告详情（自动标记已读）
- [ ] 通知中心
- [ ] 标记已读/全部已读

### 维修员
- [ ] 工作台工单列表
- [ ] 刷新工单
- [ ] 开始维修/完成维修/退单
