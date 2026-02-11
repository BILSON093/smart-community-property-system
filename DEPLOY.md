# 智慧社区物业管理系统 - 快速启动指南

## 系统概述

这是一个完整的智慧社区物业管理系统，包含四个部分：

1. **后端** - Spring Boot + MyBatis Plus
2. **Web管理端** - Vue3 + Element Plus（管理员/维修员）
3. **H5移动端** - Vue3 + Vant（业主浏览器版）
4. **微信小程序** - 微信原生小程序（业主小程序版）

## 前置要求

### 后端要求
- **JDK 8+** (已安装: OpenJDK 25.0.1)
- **Maven 3.6+** (已安装: Apache Maven 3.9.6)
- **MySQL 5.7+** (需要安装)
- **本地AI服务** (可选，用于AI客服功能)

### 前端要求
- **Node.js 16+** (需要安装)
- **npm 或 yarn** (需要安装)

## 快速启动

### 方法一：使用启动脚本（推荐）

```bash
cd /Users/macbook/Desktop/ai/elysia
./start.sh
```

脚本会自动检查环境并提示启动各个服务。

### 方法二：手动启动

#### 1. 初始化数据库

```bash
cd /Users/macbook/Desktop/ai/elysia
mysql -u root -p < database/init.sql
```

或者手动执行：
```sql
-- 登录MySQL
mysql -u root -p

-- 执行SQL脚本
source /Users/macbook/Desktop/ai/elysia/database/init.sql;
```

#### 2. 启动后端服务

```bash
cd /Users/macbook/Desktop/ai/elysia/backend

# 方式1：使用Maven运行
mvn spring-boot:run

# 方式2：打包后运行
mvn clean package
java -jar target/wuye-system-1.0.0.jar
```

后端服务将在 **http://localhost:8080/api** 启动

#### 3. 启动Web管理端

```bash
cd /Users/macbook/Desktop/ai/elysia/web-admin

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

Web管理端将在 **http://localhost:3000** 启动

#### 4. 启动H5移动端

```bash
cd /Users/macbook/Desktop/ai/elysia/mobile

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

H5移动端将在 **http://localhost:3001** 启动

#### 5. 运行微信小程序

1. 下载并安装 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 打开微信开发者工具
3. 导入项目，选择 `/Users/macbook/Desktop/ai/elysia/miniprogram` 目录
4. 填写AppID（测试号或正式号）
5. 点击"编译"按钮

## 默认账号

### 管理员
- 账号：`admin`
- 密码：`12345678`
- 访问地址：http://localhost:3000
- 角色：系统管理员

### 维修员
- 需要在Web端注册
- 注册后需管理员审核通过
- 访问地址：http://localhost:3000

### 业主
- 需要注册（示例账号：`13800138001` / `12345678`）
- H5端：http://localhost:3001
- 小程序：通过微信开发者工具运行

## 功能说明

### 管理员功能

#### 首页
- 数据概览（业主总数、待处理报修、本月缴费、今日活跃）
- 图表展示（报修类型占比、近七日缴费趋势）

#### 用户管理
- 业主管理（增删改查、重置密码）
- 维修员管理（审核、禁用、查看详情）

#### 业务管理
- 报修管理（审核、派单、查看详情）
- 缴费管理（查看账单、新增缴费）
- 通知公告（发布、编辑、删除）
- 社区活动（发布、编辑、删除）
- 轮播图管理（添加、编辑、删除）
- 论坛管理（查看、删除）

#### 客服系统
- 在线客服（人工回复、查看消息）

### 维修员功能

- 工单大厅（查看分配的工单）
- 开始维修（更新状态为"维修中"）
- 完成维修（更新状态为"已完成"）
- 个人中心（修改信息）

### 业主功能（H5/小程序）

#### 首页
- 轮播图展示
- 通知公告滚动
- 功能金刚区（物业缴费、在线报修、社区活动、联系客服、社区论坛）
- 社区活动推荐

#### 物业缴费
- 查看账单列表
- 在线支付
- 查看缴费历史

#### 在线报修
- 提交故障描述
- 上传图片（最多3张）
- 查看报修记录
- 查看报修状态

#### AI智能客服
- 智能对话
- 支持链接解析
- 可转人工客服

#### 社区论坛
- 浏览帖子
- 发布帖子（文字+图片）
- 图片预览

#### 消息中心
- 通知公告
- 系统消息

#### 个人中心
- 查看个人信息
- 快捷功能入口
- 退出登录

## 配置说明

### 后端配置

编辑 `backend/src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/wuye?useUnicode=true&characterEncoding=utf8
    username: root
    password: 12345678

file:
  upload:
    path: /Users/macbook/Desktop/ai/elysia/upload
    url-prefix: http://localhost:8080/upload

jwt:
  secret: wuye-system-secret-key-2024
  expiration: 86400000  # 24小时

ai:
  url: http://localhost:1234/v1/chat/completions
  model: local-model
```

### 前端配置

#### Web管理端

编辑 `web-admin/vite.config.js`:

```javascript
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

#### H5移动端

编辑 `mobile/vite.config.js`:

```javascript
export default defineConfig({
  server: {
    port: 3001,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

#### 微信小程序

编辑 `miniprogram/app.js`:

```javascript
App({
  globalData: {
    baseUrl: 'http://localhost:8080/api'  // 修改为你的后端地址
  }
})
```

## 常见问题

### 1. 后端启动失败

**问题**: 找不到MySQL驱动
```
解决: 检查pom.xml中MySQL依赖是否正确
mvn clean install -U
```

**问题**: 数据库连接失败
```
解决:
1. 检查MySQL服务是否启动
2. 检查application.yml中的数据库配置
3. 检查用户名密码是否正确
```

### 2. 前端启动失败

**问题**: npm install失败
```
解决:
1. 检查网络连接
2. 切换npm源: npm config set registry https://registry.npmmirror.com
3. 清除缓存: npm cache clean --force
```

**问题**: 端口被占用
```
解决:
1. 修改vite.config.js中的端口号
2. 或者关闭占用端口的进程
```

### 3. 微信小程序问题

**问题**: 网络请求失败
```
解决:
1. 在小程序后台配置服务器域名
2. 开发时可勾选"不校验合法域名"
```

**问题**: 无法登录
```
解决:
1. 检查后端服务是否启动
2. 检查baseUrl配置是否正确
3. 查看console错误信息
```

### 4. 数据库问题

**问题**: 表已存在
```
解决: 删除数据库重新创建
DROP DATABASE wuye;
CREATE DATABASE wuye;
source database/init.sql;
```

**问题**: 测试数据不正确
```
解决: 修改database/init.sql中的测试数据
```

## 生产部署

### 后端部署

1. 修改配置（数据库、文件路径等）
2. 打包: `mvn clean package`
3. 运行: `java -jar wuye-system-1.0.0.jar`
4. 使用nohup或systemd管理服务

### 前端部署

1. 构建生产版本: `npm run build`
2. 将dist目录部署到Nginx
3. 配置Nginx反向代理

#### Nginx配置示例

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

### 小程序发布

1. 微信开发者工具中点击"上传"
2. 填写版本号和项目备注
3. 提交审核
4. 审核通过后发布

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

## 技术支持

- 后端文档: `backend/README.md`
- Web管理端文档: `web-admin/README.md`
- H5移动端文档: `mobile/README.md`
- 微信小程序文档: `miniprogram/README.md`

## 许可证

MIT License
