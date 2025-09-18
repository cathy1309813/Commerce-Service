package com.gtalent.commerce.service.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition  //一個專案只能加一次，放哪裡都可以
@Configuration
public class SpringDocConfig {
    //建立 OpenAPI 的全域設定
    //設定 API 文件的標題、描述、版本，以及安全性設定 (JWT Bearer Token) 怎麼顯示
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("E-Commerce Admin Dashboard APIs")
                        .description("根據 https://marmelab.com/react-admin-demo，使用Springboot + MySQL 開發管理後台API系統，並以Swagger 呈現API文件。")
                        .version("v1.0.0")

                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                );
    }

    //設定要分組的 API 文件
    //GroupedOpenApi 允許我們把不同路徑的 API 分開成不同群組
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("public-api")  //群組名稱
                .pathsToMatch("/commerce-service/users/**", "/commerce-service/user-segments/**",
                        "/commerce-service/categories/**", "/commerce-service/products/**",
                        "/commerce-service/reviews/**", "/jwt/**")
                .build();
    }

}
