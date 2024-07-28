package com.barneymorris.ecommerce.payment;

import com.barneymorris.ecommerce.customer.CustomerResponse;
import com.barneymorris.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
