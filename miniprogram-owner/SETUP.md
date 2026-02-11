# 微信小程序配置和使用指南

## 前置要求

1. 安装微信开发者工具
   - 下载地址：https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html

2. 拥有微信小程序账号
   - 注册地址：https://mp.weixin.qq.com/

## 配置步骤

### 1. 导入项目

1. 打开微信开发者工具
2. 选择"导入项目"
3. 选择项目目录：`/Users/macbook/Desktop/ai/trae/elysia1/miniprogram-owner`
4. 填写项目名称（例如：智慧社区业主端）
5. 填写AppID（开发阶段可以使用测试号或"touristappid"）
6. 点击"导入"

### 2. 配置后端接口

打开 `utils/request.js` 文件，修改 `baseURL` 为实际的后端服务地址：

```javascript
globalData: {
  userInfo: null,
  token: null,
  baseURL: 'http://localhost:8080/api'  // 修改为实际地址
}
```

### 3. 配置网络域名

**开发阶段：**
在微信开发者工具中：
1. 点击右上角"详情"
2. 选择"本地设置"
3. 勾选"不校验合法域名、web-view（业务域名）、TLS版本以及HTTPS证书"

**正式发布：**
1. 登录微信公众平台
2. 进入"开发" -> "开发管理" -> "开发设置" -> "服务器域名"
3. 添加以下域名：
   - request合法域名：`https://your-domain.com`（实际后端域名）

### 4. 配置图片资源

将以下图片资源放入 `images/` 文件夹：

**必需的图片：**
- `logo.png` - 应用Logo (200x200)
- `avatar-default.png` - 默认头像 (120x120)
- `ai-avatar.png` - AI客服头像 (120x120)

**底部导航图标 (48x48)：**
- `tab/home.png` - 首页图标
- `tab/home-active.png` - 首页图标(激活)
- `tab/repair.png` - 报修图标
- `tab/repair-active.png` - 报修图标(激活)
- `tab/forum.png` - 论坛图标
- `tab/forum-active.png` - 论坛图标(激活)
- `tab/profile.png` - 个人中心图标
- `tab/profile-active.png` - 个人中心图标(激活)

**临时方案：**
如果没有图片资源，可以：
1. 使用emoji表情代替
2. 使用纯色背景
3. 使用网络图片URL

### 5. 测试后端接口

确保后端服务正常运行，并可以访问以下接口：
- POST `/user/login` - 用户登录
- POST `/user/register/owner` - 业主注册
- GET `/notice/list` - 通知列表
- 等等...

## 运行项目

1. 点击微信开发者工具顶部的"编译"按钮
2. 在左侧模拟器中查看小程序效果
3. 可以使用调试工具查看日志和错误信息

## 常见问题

### 问题1：网络请求失败

**原因：**
- 后端服务未启动
- 接口地址配置错误
- 网络域名未配置

**解决：**
1. 确认后端服务已启动
2. 检查 `utils/request.js` 中的 `baseURL` 配置
3. 在开发工具中勾选"不校验合法域名"

### 问题2：图片加载失败

**原因：**
- 图片资源未添加
- 图片路径错误

**解决：**
1. 检查 `images/` 文件夹是否包含所需图片
2. 使用临时方案（emoji或网络图片）

### 问题3：登录后无法访问需要登录的页面

**原因：**
- token未正确存储
- token已过期

**解决：**
1. 清除缓存，重新登录
2. 检查后端token有效期设置

### 问题4：WXML编译错误

**已修复的问题：**
- ✅ `repeat()` 方法不支持，已在JS中处理
- ✅ `app.globalData.userInfo` 在WXML中不能直接访问，已改用 `isLoggedIn` 状态

## 功能测试清单

### 基础功能
- [ ] 用户登录
- [ ] 用户注册
- [ ] 退出登录
- [ ] 修改密码

### 首页功能
- [ ] 轮播图显示
- [ ] 快捷入口跳转
- [ ] 通知列表
- [ ] 活动列表

### 报修功能
- [ ] 提交报修
- [ ] 查看报修列表
- [ ] 查看报修详情
- [ ] 评价维修服务

### 缴费功能
- [ ] 查看缴费列表
- [ ] 筛选缴费状态
- [ ] 在线支付

### 论坛功能
- [ ] 浏览帖子
- [ ] 发布帖子
- [ ] 帖子评论
- [ ] 点赞帖子

### 其他功能
- [ ] 留言反馈
- [ ] AI客服对话
- [ ] 查看活动详情
- [ ] 查看通知详情

## 项目结构说明

```
miniprogram-owner/
├── app.js                  # 小程序入口
├── app.json                # 小程序配置
├── app.wxss                # 全局样式
├── pages/                  # 页面目录
│   ├── index/             # 首页
│   ├── login/             # 登录页
│   ├── register/          # 注册页
│   ├── repair/            # 报修管理
│   ├── pay/               # 缴费管理
│   ├── forum/             # 论坛
│   ├── news/              # 通知公告
│   ├── activity/          # 社区活动
│   ├── feedback/          # 留言反馈
│   ├── chat/              # AI客服
│   └── profile/           # 个人中心
├── utils/                 # 工具类
│   ├── request.js         # 网络请求
│   ├── api.js            # API接口
│   └── util.js           # 工具函数
└── images/                # 图片资源
```

## 更新日志

### v1.0.1 (2026-01-28)
- 修复WXML编译错误（`repeat()` 方法不支持）
- 修复 `webviewScriptError` 错误
- 优化数据初始化逻辑
- 添加登录状态检查
- 改进错误处理

### v1.0.0 (2026-01-28)
- 初始版本发布
- 完成所有核心功能

## 技术支持

如有问题，请参考：
- 微信小程序官方文档：https://developers.weixin.qq.com/miniprogram/dev/framework/
- 后端API文档：`backend-api-database-doc.md`
