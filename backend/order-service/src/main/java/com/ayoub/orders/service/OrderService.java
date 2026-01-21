package com.ayoub.orders.service;

import com.ayoub.orders.domain.*;
import com.ayoub.orders.repo.OrderAuditRepository;
import com.ayoub.orders.repo.OrderIdempotencyRepository;
import com.ayoub.orders.repo.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderAuditRepository auditRepo;
    private final OrderIdempotencyRepository idemRepo;

    public OrderService(OrderRepository orderRepo, OrderAuditRepository auditRepo, OrderIdempotencyRepository idemRepo) {
        this.orderRepo = orderRepo;
        this.auditRepo = auditRepo;
        this.idemRepo = idemRepo;
    }

    @Transactional
    public OrderEntity create(String customerName, String idempotencyKeyOrNull) {
        String requestHash = sha256("customerName=" + customerName);

        if (idempotencyKeyOrNull != null && !idempotencyKeyOrNull.isBlank()) {
            Optional<OrderIdempotencyEntity> existing = idemRepo.findById(idempotencyKeyOrNull);

            if (existing.isPresent()) {
                OrderIdempotencyEntity hit = existing.get();
                if (!hit.getRequestHash().equals(requestHash)) {
                    throw new IdempotencyConflictException(idempotencyKeyOrNull);
                }
                return getOrThrow(hit.getOrderId());
            }
        }

        OrderEntity e = new OrderEntity();
        e.setId(UUID.randomUUID());
        e.setCustomerName(customerName);
        e.setStatus(OrderStatus.CREATED);
        e.setCreatedAt(Instant.now());

        OrderEntity saved = orderRepo.save(e);

        audit(saved.getId(), "ORDER_CREATED", null, saved.getStatus().name(), "Created");

        if (idempotencyKeyOrNull != null && !idempotencyKeyOrNull.isBlank()) {
            OrderIdempotencyEntity idem = new OrderIdempotencyEntity();
            idem.setIdempotencyKey(idempotencyKeyOrNull);
            idem.setRequestHash(requestHash);
            idem.setOrderId(saved.getId());
            idem.setCreatedAt(Instant.now());
            idemRepo.save(idem);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrThrow(UUID id) {
        return orderRepo.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<OrderEntity> list(OrderStatus status, Pageable pageable) {
        if (status == null) return orderRepo.findAll(pageable);
        return orderRepo.findAllByStatus(status, pageable);
    }

    @Transactional
    public OrderEntity updateStatus(UUID id, OrderStatus newStatus, String reason) {
        OrderEntity e = getOrThrow(id);
        OrderStatus old = e.getStatus();

        if (!isAllowedTransition(old, newStatus)) {
            throw new InvalidOrderStatusTransitionException(id, old, newStatus);
        }

        e.setStatus(newStatus);

        if (newStatus == OrderStatus.PAID) {
            e.setPaidAt(Instant.now());
        }

        if (newStatus == OrderStatus.CANCELLED) {
            e.setCancelledAt(Instant.now());
            e.setCancelReason(reason);
        }

        OrderEntity saved = orderRepo.save(e);
        audit(saved.getId(), "STATUS_CHANGED", old.name(), newStatus.name(), reason == null ? "" : reason);

        return saved;
    }

    private boolean isAllowedTransition(OrderStatus from, OrderStatus to) {
        if (from == to) return true;

        if (from == OrderStatus.CREATED) {
            return to == OrderStatus.PAID || to == OrderStatus.CANCELLED;
        }

        if (from == OrderStatus.PAID) {
            return false;
        }

        if (from == OrderStatus.CANCELLED) {
            return false;
        }

        return false;
    }

    private void audit(UUID orderId, String eventType, String from, String to, String note) {
        OrderAuditEntity a = new OrderAuditEntity();
        a.setOrderId(orderId);
        a.setEventType(eventType);
        a.setFromStatus(from);
        a.setToStatus(to);
        a.setNote(note);
        a.setCreatedAt(Instant.now());
        auditRepo.save(a);
    }

    private String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

     public List<OrderAuditEntity> getAudit(UUID orderId) {
        return auditRepo.findByOrderIdOrderByCreatedAtDesc(orderId);
    }
}
