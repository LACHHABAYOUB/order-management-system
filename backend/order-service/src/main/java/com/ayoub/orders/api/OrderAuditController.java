package com.ayoub.orders.api;

import com.ayoub.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderAuditController {

    private final OrderService service;

    public OrderAuditController(OrderService service) {
        this.service = service;
    }

    @Operation(summary = "Get order audit history")
    @GetMapping("/{id}/audit")
    public List<OrderAuditResponse> audit(@PathVariable UUID id) {
        return service.getAudit(id)
                .stream()
                .map(OrderAuditResponse::from)
                .toList();
    }
}
