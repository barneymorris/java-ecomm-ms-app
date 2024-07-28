package com.barneymorris.ecommerce.order;

import com.barneymorris.ecommerce.customer.CustomerClient;
import com.barneymorris.ecommerce.exception.BusinessException;
import com.barneymorris.ecommerce.kafka.OrderConfirmation;
import com.barneymorris.ecommerce.kafka.OrderProducer;
import com.barneymorris.ecommerce.orderline.OrderLineRequest;
import com.barneymorris.ecommerce.orderline.OrderLineService;
import com.barneymorris.ecommerce.payment.PaymentClient;
import com.barneymorris.ecommerce.payment.PaymentRequest;
import com.barneymorris.ecommerce.product.ProductClient;
import com.barneymorris.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest orderRequest) {
        var customer = this.customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order: no customer found with id " + orderRequest.customerId()));

        var purchasedProducts = this.productClient.purchaseProducts(orderRequest.products());

        var order = this.orderRepository.save(orderMapper.toOrder(orderRequest));

        for (PurchaseRequest purchaseRequest : orderRequest.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(null, order.getId(), purchaseRequest.productId(), purchaseRequest.quantity())
            );
        }

        paymentClient.requestOrderPayment(
                new PaymentRequest(
                        orderRequest.amount(),
                        orderRequest.paymentMethod(),
                        order.getId(),
                        order.getReference(),
                        customer
                )
        );

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        orderRequest.reference(),
                        orderRequest.amount(),
                        orderRequest.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException("no order found with id=" + orderId));
    }
}
