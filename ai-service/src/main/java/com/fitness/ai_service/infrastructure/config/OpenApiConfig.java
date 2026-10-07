package com.fitness.ai_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;


@Configuration
public class OpenApiConfig {
    private static final Logger log = LoggerFactory.getLogger(OpenApiConfig.class);

    @Bean
    public OpenAPI aiServiceOpenApi() {
        return new OpenAPI().servers(List.of(new Server().url("/"))).info(new Info()
                .title("Fitness AI Service API")
                .version("v1")
                .description("API for reading fitness recommendations"));
    }

    @EventListener
    public void logSwaggerUrl(ServletWebServerInitializedEvent event) {
        String contextPath = event.getApplicationContext().getServletContext().getContextPath();
        String swaggerPath = event.getApplicationContext().getEnvironment()
                .getProperty("springdoc.swagger-ui.path", "/swagger-ui.html");
        log.info("Swagger UI: http://localhost:{}{}{}", event.getWebServer().getPort(), contextPath, swaggerPath);
    }
}
