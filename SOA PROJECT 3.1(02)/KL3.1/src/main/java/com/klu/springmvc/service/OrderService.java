package com.klu.springmvc.service;

import com.klu.springmvc.dto.CreateOrderRequest;
import com.klu.springmvc.dto.OrderResponse;
import com.klu.springmvc.dto.UpdateStatusRequest;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request, Long requestingUserId, String requestingUserRole);
    OrderResponse getOrderById(Long orderId, Long requestingUserId, String requestingUserRole);
    List<OrderResponse> getOrdersByUserId(Long userId, Long requestingUserId, String requestingUserRole);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long orderId, UpdateStatusRequest request);
}
