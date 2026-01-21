package com.ayoub.orders.api;

import com.ayoub.orders.domain.OrderEntity;
import com.ayoub.orders.domain.OrderStatus;
import com.ayoub.orders.service.BadRequestException;
import com.ayoub.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "status", "customerName");

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @Operation(summary = "Create order (supports Idempotency-Key)")
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest req
    ) {
        OrderEntity e = service.create(req.getCustomerName(), idempotencyKey);
        URI location = URI.create("/api/v1/orders/" + e.getId());
        return ResponseEntity.created(location).body(OrderResponse.from(e));
    }

    @Operation(summary = "Get order by id")
    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable UUID id) {
        return OrderResponse.from(service.getOrThrow(id));
    }

    @Operation(summary = "List orders with pagination (optional status filter)")
    @GetMapping
    public PagedResponse<OrderResponse> list(
            @RequestParam(name = "status", required = false) OrderStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        validateSort(pageable.getSort());

        Page<OrderResponse> page = service.list(status, pageable).map(OrderResponse::from);
        return PagedResponse.from(page);
    }

    @Operation(summary = "Update order status (CREATED->PAID or CREATED->CANCELLED only)")
    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateOrderStatusRequest req) {
        if (req.getStatus() == OrderStatus.CANCELLED) {
            String reason = req.getReason();
            if (reason == null || reason.isBlank()) {
                throw new BadRequestException("cancel reason is required when status=CANCELLED", 
                        java.util.Map.of("reason", "required"));
            }
        }

        OrderEntity updated = service.updateStatus(id, req.getStatus(), req.getReason());
        return OrderResponse.from(updated);
    }

    private void validateSort(Sort sort) {
        for (Sort.Order o : sort) {
            if (!ALLOWED_SORTS.contains(o.getProperty())) {
                throw new BadRequestException("Invalid sort field", java.util.Map.of(
                        "allowed", ALLOWED_SORTS,
                        "given", o.getProperty()
                ));
            }
        }
    }
}
