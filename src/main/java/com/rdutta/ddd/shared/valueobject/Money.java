package com.rdutta.ddd.shared.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount) {

    public static final Money ZERO =
            new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(amount);

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Money cannot be negative"
            );
        }
    }

    public Money add(Money other) {
        return new Money(
                amount.add(other.amount)
        );
    }

    public Money multiply(int quantity) {
        return new Money(
                amount.multiply(
                        BigDecimal.valueOf(quantity)
                )
        );
    }
}
