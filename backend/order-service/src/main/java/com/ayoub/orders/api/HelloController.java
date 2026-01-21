package com.ayoub.orders.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Operation(summary = "Health demo endpoint")
    @GetMapping("/api/v1/hello")
    public String hello() {
        return "order-service: OK";
    }
}
