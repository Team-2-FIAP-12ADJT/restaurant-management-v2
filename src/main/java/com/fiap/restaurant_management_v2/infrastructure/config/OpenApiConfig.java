package com.fiap.restaurant_management_v2.infrastructure.config;

import com.fiap.restaurant_management_v2.infrastructure.web.ApiPaths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi v1OpenApi() {
        return GroupedOpenApi.builder()
            .group("v1")
            .displayName("Restaurant Management API v1")
            .pathsToMatch(ApiPaths.V1 + "/**")
            .addOpenApiCustomizer(openApi -> {
                openApi
                    .info(
                        new Info()
                            .title("Restaurant Management API")
                            .version("v1")
                            .description(
                                "Public API versioned by URI under /api/v1."
                            )
                    )
                    .addServersItem(
                        new Server().url("/").description("Current server")
                    );

                io.swagger.v3.oas.models.Components components = openApi.getComponents();
                if (components == null) {
                    components = new io.swagger.v3.oas.models.Components();
                }
                components.addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                );
                openApi.components(components);
                openApi.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
            })
            .build();
    }
}
