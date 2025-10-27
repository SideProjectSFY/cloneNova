package com.ssafy.clonenova.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(swaggerApiInfo())
                .servers(List.of(
                        // 로컬 환경 시 open API 문서
                        new Server().url("http://localhost:8080").description("local")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("CloneNova Repo")
                        .url("https://github.com/SideProjectSFY/cloneNova.git")
                );
    }

    private Info swaggerApiInfo() {
        return new Info()
                .title("CloneNova API")
                .version("v1.0.0")
                .description("코드 타자게임 API 문서");
    }
}
