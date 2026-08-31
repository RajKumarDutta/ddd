package com.rdutta.ddd.ordering.application.service;

import com.rdutta.ddd.ordering.application.port.inbound.CheckoutOrderUseCase;
import com.rdutta.ddd.ordering.application.port.inbound.CreateOrderCommand;
import com.rdutta.ddd.ordering.application.port.outbound.OrderRepository;
import com.rdutta.ddd.ordering.domain.model.Order;
import com.rdutta.ddd.ordering.domain.model.OrderItem;
import com.rdutta.ddd.ordering.domain.valueobject.OrderId;
import com.rdutta.ddd.shared.valueobject.Money;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CheckoutOrderService implements CheckoutOrderUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(CheckoutOrderService.class);

    private final OrderRepository orderRepository;
    private final Tracer tracer;

    private final Counter successfulCheckouts;
    private final Counter failedCheckouts;
    private final Counter duplicateCheckouts;
    private final Timer checkoutTimer;

    private final Map<String, Order> processedRequests =
            new ConcurrentHashMap<>();

    public CheckoutOrderService(
            OrderRepository orderRepository,
            Tracer tracer,
            MeterRegistry meterRegistry
    ) {
        this.orderRepository = orderRepository;
        this.tracer = tracer;

        this.successfulCheckouts = Counter.builder(
                        "orders.checkout.success")
                .description("Number of successful order checkouts")
                .register(meterRegistry);

        this.failedCheckouts = Counter.builder(
                        "orders.checkout.failure")
                .description("Number of failed order checkouts")
                .register(meterRegistry);

        this.duplicateCheckouts = Counter.builder(
                        "orders.checkout.duplicate")
                .description("Number of duplicate checkout requests")
                .register(meterRegistry);

        this.checkoutTimer = Timer.builder(
                        "orders.checkout.duration")
                .description("Order checkout processing duration")
                .register(meterRegistry);
    }

    @Override
    @Transactional
    public OrderId checkout(
            CreateOrderCommand command,
            String idempotencyKey
    ) {

        return checkoutTimer.record(() -> {

            Span span = tracer.spanBuilder("order.checkout")
                    .startSpan();

            try (var scope = span.makeCurrent()) {

                span.setAttribute(
                        "order.idempotency_key",
                        idempotencyKey
                );

                log.info(
                        "Order checkout started"
                );

                synchronized (processedRequests) {

                    Order existingOrder =
                            processedRequests.get(idempotencyKey);

                    if (existingOrder != null) {

                        duplicateCheckouts.increment();

                        log.info(
                                "Duplicate checkout request: orderId={}",
                                existingOrder.getOrderId().orderId()
                        );

                        span.setAttribute(
                                "order.duplicate",
                                true
                        );

                        return existingOrder.getOrderId();
                    }

                    if (command.items().isEmpty()) {

                        failedCheckouts.increment();

                        log.warn(
                                "Order checkout rejected: no order items"
                        );

                        span.setAttribute(
                                "order.checkout.result",
                                "rejected"
                        );

                        throw new IllegalArgumentException(
                                "Order must contain at least one item"
                        );
                    }

                    List<OrderItem> orderItems =
                            command.items()
                                    .stream()
                                    .map(item ->
                                            new OrderItem(
                                                    item.productName(),
                                                    new Money(item.unitPrice()),
                                                    item.quantity()
                                            )
                                    )
                                    .toList();

                    Order order =
                            new Order(orderItems);

                    Order savedOrder =
                            orderRepository.save(order);

                    processedRequests.put(
                            idempotencyKey,
                            savedOrder
                    );

                    successfulCheckouts.increment();

                    span.setAttribute(
                            "order.id",
                            savedOrder.getOrderId().orderId().toString()
                    );

                    span.setAttribute(
                            "order.checkout.result",
                            "success"
                    );

                    log.info(
                            "Order checkout completed: orderId={}",
                            savedOrder.getOrderId().orderId()
                    );

                    return savedOrder.getOrderId();
                }

            } catch (RuntimeException exception) {

                failedCheckouts.increment();

                log.error(
                        "Order checkout failed",
                        exception
                );

                span.recordException(exception);
                span.setAttribute(
                        "order.checkout.result",
                        "failure"
                );

                throw exception;

            } finally {
                span.end();
            }
        });
    }
}