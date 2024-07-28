package com.barneymorris.ecommerce.kafka;

import com.barneymorris.ecommerce.customer.CustomerResponse;
import com.barneymorris.ecommerce.order.PaymentMethod;
import com.barneymorris.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
