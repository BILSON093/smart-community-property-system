# 智慧社区物业管理系统

## 项目概述

这是一个完整的智慧社区物业管理系统，包含三个部分：
- **后端**：Spring Boot 2.7 + MyBatis Plus + MySQL
- **Web管理端**：Vue3 + Element Plus（管理员和维修员使用）
- **H5移动端**：Vue3 + Vant（业主使用）

## 系统功能

### 管理员功能
- 首页数据概览（业主数、报修单、缴费额等）
- 业主管理（增删改查）
- 维修员管理（审核、禁用）
- 报修管理（审核、派单、查看）
- 缴费管理（查看、新增）
- 通知公告发布
- 社区活动管理
- 轮播图管理
- 论坛管理
- 在线客服（人工回复）

### 维修员功能
- 查看分配的工单
- 开始维修
- 完成维修
- 个人中心（修改信息）

### 业主功能（移动端）
- 首页浏览（轮播图、通知公告、活动推荐）
- 物业缴费（查看账单、在线支付）
- 在线报修（提交故障、上传图片）
- 智能客服（AI对话、转人工）
- 社区论坛（发帖、浏览）
- 个人中心

## 技术栈

### 后端
- Spring Boot 2.7.14
- MyBatis Plus 3.5.3.1
- MySQL
- JWT认证
- Hutool工具库

### Web管理端
- Vue 3.3.4
- Vant 4.7.1（移动端）/ Element Plus 2.3.14（管理端）
- Vue Router 4.2.4
- Pinia 2.1.6
- Axios 1.5.0
- ECharts 5.4.3

## 项目结构

```
elysia/
├── database/           # 数据库SQL脚本
│   └── init.sql
├── backend/            # Spring Boot后端
│   ├── src/main/java/com/wye/
│   │   ├── common/     # 通用类（Result等）
│   │   ├── config/     # 配置类
│   │   ├── controller/ # 控制器
│   │   ├── dto/        # 数据传输对象
│   │   ├── entity/     # 实体类
│   │   ├── interceptor/# 拦截器
│   │   ├── mapper/     # MyBatis Mapper
│   │   ├── service/    # 服务层
│   │   └── util/       # 工具类
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
├── web-admin/          # Vue3 Web管理端
│   ├── src/
│   │   ├── layout/     # 布局组件
│   │   ├── router/     # 路由
│   │   ├── utils/      # 工具
│   │   ├── views/      # 页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
└── mobile/             # Vue3 H5移动端
    ├── src/
    │   ├── layout/     # 布局
    │   ├── router/     # 路由
    │   ├── utils/      # 工具
    │   ├── views/      # 页面
    │   ├── App.vue
    │   └── main.js
    ├── package.json
    └── vite.config.js
```

## 快速开始

### 1. 数据库初始化

```bash
# 创建数据库并导入数据
mysql -u root -p12345678 < database/init.sql
```

### 2. 启动后端服务

```bash
cd backend

# 修改数据库配置（如果需要）
# 编辑 src/main/resources/application.yml

# 启动Spring Boot应用
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/wuye-system-1.0.0.jar
```

后端服务将在 http://localhost:8080/api 启动

默认管理员账号：
- 账号：admin
- 密码：12345678

### 3. 启动Web管理端

```bash
cd web-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

Web管理端将在 http://localhost:3000 启动

### 4. 启动H5移动端

```bash
cd mobile

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

H5移动端将在 http://localhost:3001 启动

## 核心功能说明

### AI客服实现

AI客服采用RAG（检索增强生成）模式：
1. 从数据库检索用户相关信息（欠费、通知等）
2. 将检索结果作为上下文构造Prompt
3. 调用本地AI接口生成回复
4. 支持Markdown链接解析，点击可跳转

### 文件上传

- 图片上传到本地 `/upload` 目录
- 返回完整URL供前端使用
- 支持多图上传

### 权限控制

- JWT Token认证
- 拦截器控制访问权限
- 游客可访问白名单页面
- 登录用户可访问业务页面

## 数据库设计

系统包含11个核心表：

**用户相关**
- sys_user：系统用户表
- sys_repair_worker：维修员扩展表
- sys_owner：业主扩展表

**业务相关**
- bus_carousel：轮播图管理
- bus_notice：通知公告
- bus_activity：社区活动
- bus_fee：缴费信息
- bus_repair：报修工单
- bus_evaluation：服务评价
- bus_forum：论坛帖子

**客服相关**
- chat_record：聊天记录

## API文档

主要API接口：

- POST /api/user/login - 用户登录
- POST /api/user/register/worker - 维修员注册
- POST /api/common/upload - 文件上传
- POST /api/ai/chat - AI客服对话
- GET /api/notice/list - 通知列表
- GET /api/activity/list - 活动列表
- GET /api/fee/my - 我的缴费
- POST /api/fee/pay/{id} - 缴费
- GET /api/repair/list - 报修列表
- POST /api/repair/add - 提交报修
- POST /api/repair/dispatch - 派单
- GET /api/forum/list - 论坛列表

## 部署说明

### 后端部署

1. 修改 `application.yml` 中的数据库连接信息
2. 打包：`mvn clean package`
3. 运行：`java -jar wuye-system-1.0.0.jar`

### 前端部署

1. 构建生产版本：`npm run build`
2. 将 `dist` 目录部署到Nginx或Apache

### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # Web管理端
    location /admin {
        alias /path/to/web-admin/dist;
        try_files $uri $uri/ /admin/index.html;
    }

    # H5移动端
    location / {
        alias /path/to/mobile/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 图片资源
    location /upload {
        alias /path/to/upload;
    }
}
```

## 注意事项

1. 请确保MySQL服务已启动
2. 修改 `application.yml` 中的数据库连接信息
3. 确保本地AI服务已配置（如需使用AI客服）
4. 文件上传目录需要有写权限
5. 生产环境请修改JWT密钥

## 开发说明

### 后端开发

- 使用MyBatis Plus简化数据库操作
- 遵循RESTful API设计规范
- 统一使用Result类封装响应
- 使用JWT进行身份认证

### 前端开发

- 使用Composition API编写Vue3组件
- 使用Pinia进行状态管理
- 使用Axios进行HTTP请求
- 遵循路由守卫进行权限控制

## 许可证

MIT License
