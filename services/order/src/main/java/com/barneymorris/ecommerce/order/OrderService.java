package com.barneymorris.ecommerce.order;

import com.barneymorris.ecommerce.customer.CustomerClient;
import com.barneymorris.ecommerce.exception.BusinessException;
import com.barneymorris.ecommerce.orderline.OrderLineRequest;
import com.barneymorris.ecommerce.orderline.OrderLineService;
import com.barneymorris.ecommerce.product.ProductClient;
import com.barneymorris.ecommerce.product.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderLineService orderLineService;

    public Integer createOrder(OrderRequest orderRequest) {
        var customer = this.customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order: no customer found with id " + orderRequest.customerId()));

        this.productClient.purchaseProducts(orderRequest.products());

        var order = this.orderRepository.save(orderMapper.toOrder(orderRequest));

        for (PurchaseRequest purchaseRequest : orderRequest.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(null, order.getId(), purchaseRequest.productId(), purchaseRequest.quantity())
            );
        }

        return null;
    }
}
