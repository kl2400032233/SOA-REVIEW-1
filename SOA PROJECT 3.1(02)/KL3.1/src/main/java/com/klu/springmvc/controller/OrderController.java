package com.klu.springmvc.controller;

import com.klu.springmvc.dto.CreateOrderRequest;
import com.klu.springmvc.dto.OrderResponse;
import com.klu.springmvc.dto.UpdateStatusRequest;
import com.klu.springmvc.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long requestingUserId,
            @RequestHeader(value = "X-User-Role", required = false) String requestingUserRole) {
        OrderResponse response = orderService.createOrder(request, requestingUserId, requestingUserRole);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long requestingUserId,
            @RequestHeader(value = "X-User-Role", required = false) String requestingUserRole) {
        OrderResponse response = orderService.getOrderById(id, requestingUserId, requestingUserRole);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long requestingUserId,
            @RequestHeader(value = "X-User-Role", required = false) String requestingUserRole) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId, requestingUserId, requestingUserRole);
        return ResponseEntity.ok(orders);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }
}
