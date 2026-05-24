package com.wye.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wuyeOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("智慧社区物业管理系统 API")
                .description("业主、管理员、维修员三端共用 REST API，包含报修、缴费、通知、论坛、活动和 AI Agent。")
                .version("1.0.0")
                .license(new License().name("Source Available Non-Commercial License v1.0")
                    .url("https://github.com/BILSON093/smart-community-property-system/blob/main/LICENSE")));
    }
}
