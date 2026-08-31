package com.rdutta.ddd.ordering.api.web;

import com.rdutta.ddd.ordering.api.web.dto.CreateOrderRequest;
import com.rdutta.ddd.ordering.api.web.dto.OrderResponse;
import com.rdutta.ddd.ordering.application.port.inbound.CheckoutOrderUseCase;
import com.rdutta.ddd.ordering.application.port.inbound.CreateOrderCommand;
import com.rdutta.ddd.ordering.application.port.inbound.CreateOrderItemCommand;
import com.rdutta.ddd.ordering.domain.valueobject.OrderId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CheckoutOrderUseCase checkoutOrderUseCase;

    public OrderController(
            CheckoutOrderUseCase checkoutOrderUseCase
    ) {
        this.checkoutOrderUseCase = checkoutOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> checkout(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CreateOrderRequest request
    ) {


            CreateOrderCommand command =
                    new CreateOrderCommand(
                            request.items()
                                    .stream()
                                    .map(item ->
                                            new CreateOrderItemCommand(
                                                    item.productName(),
                                                    item.unitPrice(),
                                                    item.quantity()
                                            )
                                    )
                                    .toList()
                    );

            OrderId orderId =
                    checkoutOrderUseCase.checkout(command, idempotencyKey);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new OrderResponse(orderId.orderId().toString()));
        }
}
