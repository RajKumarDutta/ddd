package com.rdutta.ddd.ordering.domain.model;

import com.rdutta.ddd.shared.valueobject.Money;
import lombok.Getter;

import java.util.Objects;

@Getter
public class OrderItem {
    private final String productName;
    private final Money unitPrice;
    private final int quantity;

    public OrderItem(
            String productName,
            Money unitPrice,
            int quantity
    ) {
        this.productName = Objects.requireNonNull(
                productName,
                "Product name cannot be null"
        );

        this.unitPrice = Objects.requireNonNull(
                unitPrice,
                "Unit price cannot be null"
        );

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        this.quantity = quantity;
    }

    public Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }
}