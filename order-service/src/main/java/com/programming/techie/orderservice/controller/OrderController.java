package com.programming.techie.orderservice.controller;

import com.programming.techie.orderservice.client.InventoryClient;
import com.programming.techie.orderservice.dto.OrderDto;
import com.programming.techie.orderservice.model.Order;
import com.programming.techie.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;
    private final StreamBridge streamBridge;

    private final ExecutorService traceablExecutorService;

    @PostMapping
    public String placeOrder(@RequestBody OrderDto dto) {
        circuitBreakerFactory.configureExecutorService(traceablExecutorService);
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("inventory");

        Supplier<Boolean> booleanSupplier = () -> dto.getOrderLineItemsList().stream()
                .allMatch(lines -> {
                    log.info("Making call to inventory service for skuCode {}", lines.getSkuCode());
                    return inventoryClient.checkStock(lines.getSkuCode());
                });

        boolean allProductsInStock = circuitBreaker.run(booleanSupplier, throwable -> handleErrorCase());

        if (allProductsInStock) {
            Order order = new Order();
            order.setOrderLineItems(dto.getOrderLineItemsList());
            order.setOrderNumber(UUID.randomUUID().toString());

            orderRepository.save(order);

            log.info("Sending Order Details to Notification Service");
            streamBridge.send("notificationEventSupplier-out-0", MessageBuilder.withPayload(order.getId()).build());

            return "Order place successfully";
        }

        return "Order failed, one of the products in the order is not in stock";
    }

    private Boolean handleErrorCase() {
        return false;
    }
}
