package com.ayoub.orders.api;

import com.ayoub.orders.domain.OrderEntity;
import com.ayoub.orders.domain.OrderStatus;
import com.ayoub.orders.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {OrderController.class, HelloController.class})
class OrderControllerTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mvc;

    @MockBean
    private OrderService orderService;

    private static OrderEntity sample(UUID id, String name, OrderStatus status) {
        OrderEntity e = new OrderEntity();
        e.setId(id);
        e.setCustomerName(name);
        e.setStatus(status);
        e.setCreatedAt(Instant.parse("2026-01-21T00:00:00Z"));
        return e;
    }

    @Test
    void create_shouldValidateAndReturn200or201() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(orderService.create(eq("Ayoub"), null)).thenReturn(sample(id, "Ayoub", OrderStatus.CREATED));

        mvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerName\":\"Ayoub\"}"))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.customerName").value("Ayoub"))
            .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void list_shouldReturnArrayOrPageDependingOnYourApi() throws Exception {
        Mockito.when(orderService.list()).thenReturn(List.of(
                sample(UUID.randomUUID(), "A", OrderStatus.CREATED),
                sample(UUID.randomUUID(), "B", OrderStatus.PAID)
        ));

        mvc.perform(get("/api/v1/orders"))
            .andExpect(status().isOk());
    }
}
