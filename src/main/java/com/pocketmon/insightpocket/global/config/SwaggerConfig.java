package com.pocketmon.insightpocket.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String PROD_SERVER = "https://www.tenma.store";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("InsightPocket API")
                        .description("InsightPocket Swagger/OpenAPI")
                        .version("v1"))
                .servers(List.of(
                        new Server().url(PROD_SERVER).description("prod")
                ));
    }
}