package com.ayoub.orders.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("orderdb")
            .withUsername("order_user")
            .withPassword("order_pass");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);

        r.add("spring.flyway.url", postgres::getJdbcUrl);
        r.add("spring.flyway.user", postgres::getUsername);
        r.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    MockMvc mvc;

    @Test
    void createOrder_shouldReturn201_andBody() throws Exception {
        mvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"Ayoub\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("Ayoub"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void getById_invalidUuid_shouldReturn400() throws Exception {
        mvc.perform(get("/api/v1/orders/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void idempotency_shouldReturnSameOrder() throws Exception {
        String key = "abc-123";

        String body1 = mvc.perform(post("/api/v1/orders")
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"Ayoub\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/api/v1/orders")
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"Ayoub\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(body1));
    }

    @Test
    void invalidTransition_createdToPaidThenCancel_should409() throws Exception {
        String createJson = mvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"Ayoub\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = createJson.replaceAll(".*\"id\"\\s*:\\s*\"([^\"]+)\".*", "$1");

        mvc.perform(patch("/api/v1/orders/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"PAID\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        mvc.perform(patch("/api/v1/orders/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CANCELLED\",\"reason\":\"too late\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }
}
