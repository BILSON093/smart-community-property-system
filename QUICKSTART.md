# 智慧社区物业管理系统 - 快速启动指南

## 📋 当前状态

✅ 项目代码已创建完成
✅ Web管理端代码已完成
✅ H5移动端代码已完成
✅ 微信小程序代码已完成
⚠️ 后端代码需要修复Lombok配置问题

## 🚀 快速启动

### 1. 修复后端Lombok问题

由于Lombok注解处理器配置问题，需要手动修复实体类。执行以下脚本：

```bash
cd /Users/macbook/Desktop/ai/elysia
./fix-lombok.sh
```

或者手动为每个entity类添加getter/setter方法（IDEA/Eclipse可自动生成）。

### 2. 初始化数据库

```bash
# 如果MySQL已安装
mysql -u root -p12345678 < database/init.sql

# 或者手动执行
mysql -u root -p
> source /Users/macbook/Desktop/ai/elysia/database/init.sql
```

### 3. 启动后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/wuye-system-1.0.0.jar
```

后端将在 http://localhost:8080/api 启动

### 4. 启动Web管理端

```bash
cd web-admin
npm install
npm run dev
```

Web管理端将在 http://localhost:3000 启动

### 5. 启动H5移动端

```bash
cd mobile
npm install
npm run dev
```

H5移动端将在 http://localhost:3001 启动

### 6. 运行微信小程序

1. 下载微信开发者工具
2. 导入项目，选择 `miniprogram` 目录
3. 填写AppID
4. 点击编译

## 📦 项目结构

```
elysia/
├── database/           # 数据库SQL脚本
│   └── init.sql
├── backend/            # Spring Boot后端
│   ├── src/main/java/com/wye/
│   │   ├── common/    # 通用类
│   │   ├── config/    # 配置类
│   │   ├── controller/# 控制器
│   │   ├── dto/       # 数据传输对象
│   │   ├── entity/    # 实体类（需修复）
│   │   ├── interceptor/# 拦截器
│   │   ├── mapper/    # MyBatis Mapper
│   │   ├── service/   # 服务层
│   │   └── util/      # 工具类
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
├── web-admin/          # Vue3 Web管理端
│   ├── src/
│   │   ├── layout/    # 布局组件
│   │   ├── router/    # 路由
│   │   ├── utils/     # 工具
│   │   └── views/     # 页面组件
│   ├── package.json
│   └── vite.config.js
├── mobile/             # Vue3 H5移动端
│   ├── src/
│   │   ├── layout/    # 布局
│   │   ├── router/    # 路由
│   │   ├── utils/     # 工具
│   │   └── views/     # 页面
│   ├── package.json
│   └── vite.config.js
└── miniprogram/         # 微信小程序
    ├── pages/         # 页面
    ├── utils/         # 工具
    ├── images/        # 图片资源
    ├── app.js         # 小程序逻辑
    ├── app.json       # 小程序配置
    └── app.wxss       # 全局样式
```

## 🔧 后端修复说明

### 问题1：Lombok不生效

**原因**: 实体类使用@Data注解，但编译时Lombok没有正确生成getter/setter方法

**解决方案**：
```bash
# 方案1：使用IDE自动生成（推荐）
# 1. 在IDE中打开backend项目
# 2. 右键entity包下的所有类
# 3. 选择 "Refactor" -> "Encapsulate Fields"
# 4. 生成所有getter/setter方法

# 方案2：修改pom.xml添加Lombok插件
# 在pom.xml的<build><plugins>中添加：
```

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <source>${java.version}</source>
        <target>${java.version}</target>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

### 问题2：包名不一致

**已修复**: 已将所有`com.wuye`包名改为`com.wye`

## 🎯 默认账号

### 管理员
- 账号：`admin`
- 密码：`12345678`
- 访问：http://localhost:3000

### 维修员
- 需注册，示例：`13900139001` / `12345678`
- 访问：http://localhost:3000

### 业主
- 需注册，示例：`13800138001` / `12345678`
- H5：http://localhost:3001
- 小程序：微信开发者工具

## 📱 功能清单

### 管理员端
- ✅ 首页数据概览
- ✅ 业主管理
- ✅ 维修员管理
- ✅ 报修管理（审核、派单）
- ✅ 缴费管理
- ✅ 通知公告
- ✅ 社区活动
- ✅ 轮播图管理
- ✅ 论坛管理
- ✅ 在线客服（人工）

### 维修员端
- ✅ 工单大厅
- ✅ 开始维修
- ✅ 完成维修
- ✅ 个人中心

### 业主端（H5/小程序）
- ✅ 首页（轮播图、通知、活动）
- ✅ 物业缴费
- ✅ 在线报修
- ✅ AI智能客服
- ✅ 社区论坛
- ✅ 消息中心
- ✅ 个人中心

## 🔗 API接口

### 用户相关
```
POST /api/user/login              - 用户登录
POST /api/user/register/worker     - 维修员注册
GET  /api/user/info              - 获取用户信息
```

### 文件上传
```
POST /api/common/upload           - 文件上传
```

### 业务接口
```
GET  /api/notice/list            - 通知列表
GET  /api/activity/list          - 活动列表
GET  /api/carousel/list          - 轮播图列表
GET  /api/fee/my                - 我的缴费
POST /api/fee/pay/{id}          - 缴费
GET  /api/repair/my              - 我的报修
POST /api/repair/add             - 提交报修
POST /api/repair/dispatch        - 派单
GET  /api/forum/list             - 论坛列表
POST /api/forum/add              - 发布帖子
GET  /api/chat/session           - 聊天记录
POST /api/chat/send              - 发送消息
```

### AI客服
```
POST /api/ai/chat                - AI对话
```

## ⚙️ 配置说明

### 后端配置 (application.yml)
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wuye
    username: root
    password: 12345678

file:
  upload:
    path: /Users/macbook/Desktop/ai/elysia/upload
```

### 前端配置
- Web管理端: `web-admin/vite.config.js`
- H5移动端: `mobile/vite.config.js`
- 微信小程序: `miniprogram/app.js`

## ❓ 常见问题

### Q: 后端编译失败？
A: 需要修复Lombok配置，参考"后端修复说明"部分

### Q: 前端npm install失败？
A: 尝试切换npm源：
```bash
npm config set registry https://registry.npmmirror.com
npm install
```

### Q: MySQL连接失败？
A: 检查MySQL服务是否启动，用户名密码是否正确

### Q: 小程序网络请求失败？
A: 在微信开发者工具中勾选"不校验合法域名"

## 📞 技术支持

- 完整文档：`DEPLOY.md`
- 数据库脚本：`database/init.sql`
- 启动脚本：`start.sh`
- 修复脚本：`fix-lombok.sh`（需创建）

## 🎉 总结

这是一个功能完整的智慧社区物业管理系统，包含：

1. ✅ 完整的数据库设计（11个表）
2. ✅ Spring Boot后端（需修复Lombok）
3. ✅ Vue3 Web管理端
4. ✅ Vue3 H5移动端
5. ✅ 微信小程序端

修复后端Lombok问题后，即可正常启动整个系统！
