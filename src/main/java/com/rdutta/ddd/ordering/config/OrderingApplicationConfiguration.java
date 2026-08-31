package com.rdutta.ddd.ordering.config;

import com.rdutta.ddd.ordering.application.port.outbound.OrderRepository;
import com.rdutta.ddd.ordering.application.service.CheckoutOrderService;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderingApplicationConfiguration {

    @Bean
    public CheckoutOrderService checkoutOrderService(OrderRepository orderRepository, Tracer tracer, MeterRegistry meterRegistry) {
        return new CheckoutOrderService(orderRepository, tracer, meterRegistry);
    }
}
