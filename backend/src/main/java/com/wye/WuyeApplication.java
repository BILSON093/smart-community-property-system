package com.wye;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wye.mapper")
public class WuyeApplication {
    public static void main(String[] args) {
        SpringApplication.run(WuyeApplication.class, args);
        System.out.println("\n智慧社区物业管理系统启动成功！");
        System.out.println("后端地址: http://localhost:8080/api");
    }
}
