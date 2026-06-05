package com.fiap.restaurant_management_v2.config;

import com.fiap.restaurant_management_v2.adapter.in.web.ApiPaths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exposes the public API as a versioned OpenAPI group.
 * The {@code v1} group matches {@code /api/v1/**} and is served at
 * {@code /v3/api-docs/v1}. Add a new bean per version only when a real
 * {@code /api/v2} exists — no phantom versions.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi v1OpenApi() {
        return GroupedOpenApi.builder()
                .group("v1")
                .displayName("Restaurant Management API v1")
                .pathsToMatch(ApiPaths.V1 + "/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("Restaurant Management API")
                                .version("v1")
                                .description("Public API versioned by URI under /api/v1."))
                        .addServersItem(new Server()
                                .url("/")
                                .description("Current server")))
                .build();
    }
}
