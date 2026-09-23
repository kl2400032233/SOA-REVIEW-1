package com.klu.springmvc.service.impl;

import com.klu.springmvc.client.MenuServiceClient;
import com.klu.springmvc.dto.*;
import com.klu.springmvc.model.Order;
import com.klu.springmvc.model.OrderItem;
import com.klu.springmvc.model.OrderStatus;
import com.klu.springmvc.exception.ItemUnavailableException;
import com.klu.springmvc.exception.ResourceNotFoundException;
import com.klu.springmvc.exception.UnauthorizedOrderAccessException;
import com.klu.springmvc.repository.OrderRepository;
import com.klu.springmvc.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final MenuServiceClient menuServiceClient;

    public OrderServiceImpl(OrderRepository orderRepository, MenuServiceClient menuServiceClient) {
        this.orderRepository = orderRepository;
        this.menuServiceClient = menuServiceClient;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request, Long requestingUserId, String requestingUserRole) {
        Long effectiveUserId = request.getUserId();
        if ("CUSTOMER".equalsIgnoreCase(requestingUserRole) && requestingUserId != null) {
            effectiveUserId = requestingUserId;
        }

        logger.info("Initiating order placement for userId: {}. Contacting Menu Service via Eureka...", effectiveUserId);

        Order order = new Order();
        order.setUserId(effectiveUserId);
        order.setStatus(OrderStatus.PLACED);

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItemDto menuItem = menuServiceClient.getMenuItemById(itemReq.getItemId());

            if (menuItem == null) {
                throw new ResourceNotFoundException("Menu item not found with id: " + itemReq.getItemId());
            }

            if (!"AVAILABLE".equalsIgnoreCase(menuItem.getAvailabilityStatus())) {
                logger.warn("Order placement rejected: item '{}' (id: {}) is currently UNAVAILABLE", menuItem.getName(), menuItem.getItemId());
                throw new ItemUnavailableException("Selected menu item '" + menuItem.getName() + "' is currently unavailable.");
            }

            BigDecimal price = menuItem.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            calculatedTotal = calculatedTotal.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(menuItem.getItemId());
            orderItem.setItemName(menuItem.getName());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setUnitPrice(price);
            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);
        }

        order.setTotalAmount(calculatedTotal);
        Order savedOrder = orderRepository.save(order);
        logger.info("Order successfully placed with orderId: {}, total: {}", savedOrder.getOrderId(), savedOrder.getTotalAmount());

        return OrderResponse.fromEntity(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long requestingUserId, String requestingUserRole) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if ("CUSTOMER".equalsIgnoreCase(requestingUserRole) && requestingUserId != null) {
            if (!order.getUserId().equals(requestingUserId)) {
                throw new UnauthorizedOrderAccessException("Access denied: You can only view your own orders");
            }
        }

        return OrderResponse.fromEntity(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId, Long requestingUserId, String requestingUserRole) {
        if ("CUSTOMER".equalsIgnoreCase(requestingUserRole) && requestingUserId != null) {
            if (!userId.equals(requestingUserId)) {
                throw new UnauthorizedOrderAccessException("Access denied: You can only view your own orders");
            }
        }

        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, UpdateStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        logger.info("Updating status of order #{} from {} to {}", orderId, order.getStatus(), request.getStatus());
        order.setStatus(request.getStatus());
        Order updated = orderRepository.save(order);
        return OrderResponse.fromEntity(updated);
    }
}
