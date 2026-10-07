package com.fitness.activity_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwaggerUiTest {

    @Autowired
    private ServletWebServerApplicationContext context;

    @Test
    void servesSwaggerUiAndActivityOpenApiDocument() throws Exception {
        int port = context.getWebServer().getPort();
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {
            HttpResponse<String> ui = client.send(
                    HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/swagger-ui.html")).GET().build(),
                    HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> docs = client.send(
                    HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/v3/api-docs")).GET().build(),
                    HttpResponse.BodyHandlers.ofString());

            assertEquals(200, ui.statusCode());
            assertEquals(200, docs.statusCode());
            assertTrue(docs.body().contains("/api/activities"));
            assertEquals("/", JsonMapper.builder().build().readTree(docs.body())
                    .path("servers").get(0).path("url").asText());
        }
    }
}
