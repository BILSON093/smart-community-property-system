# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

智慧社区物业管理系统，单体 Spring Boot 后端 + 三个前端客户端共享同一套 REST API。

## 常用命令

### 后端 (backend/)

```bash
cd backend
mvn spring-boot:run                    # 开发模式启动 (端口 8080, context-path /api)
mvn clean package -DskipTests          # 打包为 target/wuye-system-1.0.0.jar
java -jar target/wuye-system-1.0.0.jar # 运行打包后的 JAR
mvn test                               # 运行测试 (目前无测试文件)
```

Lombok 注解处理器问题：如编译失败，执行 `./fix-lombok.sh` 或在 pom.xml 的 maven-compiler-plugin 中添加 Lombok annotationProcessorPaths。

### web-admin (管理端)

```bash
cd web-admin
npm install        # 安装依赖
npm run dev        # 开发服务器 http://localhost:3000
npm run build      # 生产构建到 dist/
npm run preview    # 预览生产构建
```

### web-user (业主移动端)

```bash
cd web-user
npm install
npm run dev        # 开发服务器 http://localhost:3001
npm run build
npm run preview
```

### miniprogram-owner (微信小程序)

使用微信开发者工具导入 `miniprogram-owner/` 目录，点击编译运行。

### 数据库初始化

```bash
mysql -u root -p < database/wuye.sql
```

默认数据库: `wuye`, 用户名 `root`, 密码 `12345678` (配置在 `backend/src/main/resources/application.yml`)。

## 架构

### 整体结构

```
单体后端 (Spring Boot 2.7)
    ├── web-admin        (Vue3 + Element Plus, 管理员/维修员, 端口 3000)
    ├── web-user         (Vue3 + Vant, 业主移动端, 端口 3001)
    └── miniprogram-owner (原生微信小程序, 业主)
```

所有前端通过 Vite dev server proxy 将 `/api` 转发到 `localhost:8080`。

### 后端分层

```
Controller → Service → Mapper (MyBatis Plus) → MySQL
```

- `controller/` — REST 接口 (15 个)
- `service/` — 业务逻辑 (18 个)
- `mapper/` — MyBatis Plus 数据访问接口 (17 个)
- `entity/` — 数据库实体类，使用 `@Data` Lombok 注解 (18 个)
- `dto/` — 数据传输对象
- `common/Result.java` — 统一响应封装
- `interceptor/AuthInterceptor.java` — JWT 认证拦截器
- `config/` — CORS、拦截器注册、静态资源配置

### 认证机制

- JWT Token，包含 userId、username、role
- 角色: 0=管理员, 1=业主, 2=维修员
- 白名单接口 (登录、注册、公开列表) 不需要认证
- 前端通过 Axios 拦截器附加 `Authorization: Bearer <token>`
- Web 端存储在 localStorage，小程序存储在 wx.setStorageSync

### AI Agent 系统

- `agent/PropertyAgent.java` — 实现 OpenAI 兼容的 Function Calling 循环 (最多 5 轮)
- `agent/PropertyAgentTools.java` — 17 个工具 (12 个业主可用 + 5 个管理员专用)
- 工具按用户角色过滤
- 支持多 AI 提供商: 本地、ModelScope (Qwen, GLM)、DashScope
- AI 配置存储在 `ai_config` 表，可运行时切换

### 数据库表

`sys_user`, `sys_owner`, `sys_repair_worker`, `bus_carousel`, `bus_notice`, `bus_activity`, `bus_fee`, `bus_fee_settings`, `bus_repair`, `bus_evaluation`, `bus_feedback`, `bus_forum`, `bus_forum_category`, `bus_forum_comment`, `bus_forum_like`, `chat_record`, `ai_config`

MyBatis Plus 全局配置: 下划线转驼峰、逻辑删除字段 `deleted`、主键自增。

### 前端路由守卫

两个 Vue 前端都在 `router/index.js` 中实现路由守卫，检查 localStorage 中的 token 和 role，未登录时重定向到登录页。

## 关键配置

| 文件 | 作用 |
|---|---|
| `backend/src/main/resources/application.yml` | 后端全部配置 (端口、数据库、JWT、AI、文件上传) |
| `backend/pom.xml` | Maven 依赖和构建插件 |
| `web-admin/vite.config.js` | 管理端 Vite 配置 (端口 3000, API 代理) |
| `web-user/vite.config.js` | 移动端 Vite 配置 (端口 3001, API 代理) |
| `database/wuye.sql` | 完整 DDL + 种子数据 |

## 默认账号

- 管理员: `admin` / `12345678`
- 维修员/业主: 需自行注册

## 开发规范

- 后端统一使用 `Result` 类封装 API 响应
- MyBatis Plus 简化 CRUD，实体类使用 `@Data` 注解
- 前端使用 Vue3 Composition API
- 无 ESLint/Prettier 配置，无后端测试文件
