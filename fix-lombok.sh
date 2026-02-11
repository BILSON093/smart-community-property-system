#!/bin/bash

echo "======================================"
echo "  修复后端Lombok配置"
echo "======================================"
echo ""

cd backend

# 备份原pom.xml
cp pom.xml pom.xml.backup

echo "1. 添加Lombok Maven编译器插件..."
# 在spring-boot-maven-plugin之前添加maven-compiler-plugin
sed -i '' '/<plugin>/i\
\
        <plugin>\
            <groupId>org.apache.maven.plugins</groupId>\
            <artifactId>maven-compiler-plugin</artifactId>\
            <version>3.8.1</version>\
            <configuration>\
                <source>${java.version}</source>\
                <target>${java.version}</target>\
                <annotationProcessorPaths>\
                    <path>\
                        <groupId>org.projectlombok</groupId>\
                        <artifactId>lombok</artifactId>\
                        <version>1.18.24</version>\
                    </path>\
                </annotationProcessorPaths>\
            </configuration>\
        </plugin>' pom.xml

echo "2. 添加Lombok版本属性..."
sed -i '' '/<properties>/a\
        <lombok.version>1.18.24</lombok.version>' pom.xml

echo "3. 清理并重新编译..."
mvn clean compile -DskipTests

echo ""
echo "======================================"
echo "  修复完成！"
echo "======================================"
echo ""
echo "如果仍有问题，请尝试以下方法之一："
echo ""
echo "方法1：使用IDE自动生成getter/setter"
echo "  1. 在IDEA/Eclipse中打开backend项目"
echo "  2. 右键entity包下的类"
echo "  3. 选择Generate Getter and Setter"
echo "  4. 生成所有字段的方法"
echo ""
echo "方法2：手动添加@Data注解的替代方案"
echo "  也可以手动为每个实体类添加getter/setter方法"
echo ""
echo "现在尝试运行："
echo "  cd backend && mvn spring-boot:run"
echo ""
