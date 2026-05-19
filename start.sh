#!/bin/bash

echo "======================================"
echo "  智慧社区物业管理系统 - 启动脚本"
echo "======================================"
echo ""

# 检查Java
echo "1. 检查Java环境..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1)
    echo "   ✓ Java已安装: $JAVA_VERSION"
else
    echo "   ✗ Java未安装"
    exit 1
fi

# 检查Maven
echo ""
echo "2. 检查Maven环境..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version 2>&1 | grep "Apache Maven" | head -1)
    echo "   ✓ Maven已安装: $MVN_VERSION"
else
    echo "   ✗ Maven未安装"
    exit 1
fi

# 检查Node.js
echo ""
echo "3. 检查Node.js环境..."
if command -v node &> /dev/null; then
    NODE_VERSION=$(node -v)
    echo "   ✓ Node.js已安装: $NODE_VERSION"
    if command -v npm &> /dev/null; then
        NPM_VERSION=$(npm -v)
        echo "   ✓ npm已安装: $NPM_VERSION"
    fi
else
    echo "   ✗ Node.js未安装"
    echo "   提示: 前端项目需要Node.js，请访问 https://nodejs.org/ 下载安装"
fi

# 检查MySQL
echo ""
echo "4. 检查MySQL环境..."
if command -v mysql &> /dev/null; then
    echo "   ✓ MySQL已安装"
    echo ""
    read -p "是否初始化数据库？(y/n): " init_db
    if [ "$init_db" = "y" ] || [ "$init_db" = "Y" ]; then
        echo "   正在导入数据库..."
        mysql -u root -p < database/init.sql
        if [ $? -eq 0 ]; then
            echo "   ✓ 数据库导入成功"
        else
            echo "   ✗ 数据库导入失败"
            echo "   提示: 请手动执行: mysql -u root -p < database/init.sql"
        fi
    fi
else
    echo "   ✗ MySQL未安装或不在PATH中"
    echo "   提示: 请手动执行: mysql -u root -p12345678 < database/init.sql"
fi

echo ""
echo "======================================"
echo "  启动说明"
echo "======================================"
echo ""
echo "后端启动命令:"
echo "  cd backend && mvn spring-boot:run"
echo ""
echo "Web管理端启动命令:"
echo "  cd web-admin && npm install && npm run dev"
echo ""
echo "H5移动端启动命令:"
echo "  cd mobile && npm install && npm run dev"
echo ""
echo "微信小程序:"
echo "  1. 打开微信开发者工具"
echo "  2. 导入项目，选择 miniprogram 目录"
echo "  3. 填写AppID"
echo "  4. 点击编译运行"
echo ""
echo "默认管理员账号:"
echo "  账号: admin"
echo "  密码: 12345678"
echo ""
echo "======================================"
echo ""

# 询问是否启动后端
read -p "是否启动后端服务？(y/n): " start_backend
if [ "$start_backend" = "y" ] || [ "$start_backend" = "Y" ]; then
    echo ""
    echo "正在启动后端服务..."
    echo "提示: 后端将在 http://localhost:8080/api 启动"
    echo "按 Ctrl+C 停止服务"
    echo ""
    cd backend
    mvn spring-boot:run
fi
