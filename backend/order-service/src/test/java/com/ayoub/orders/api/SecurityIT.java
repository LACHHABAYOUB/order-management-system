package com.ayoub.orders.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
       
})
class SecurityIT {

    @Autowired
    MockMvc mvc;

    private static final String KEY = "secret-api-key";

    @Test
    void hello_shouldBePublic() throws Exception {
        mvc.perform(get("/api/v1/hello"))
            .andExpect(status().isOk());
    }

    @Test
    void createOrder_withoutKey_should401() throws Exception {
        mvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerName\":\"Ayoub\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void createOrder_withWrongKey_should401() throws Exception {
        mvc.perform(post("/api/v1/orders")
                .header("X-API-KEY", "wrong")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerName\":\"Ayoub\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void createOrder_withCorrectKey_shouldSucceed() throws Exception {
        mvc.perform(post("/api/v1/orders")
                .header("X-API-KEY", KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerName\":\"Ayoub\"}"))
            .andExpect(status().is2xxSuccessful());
    }
}
