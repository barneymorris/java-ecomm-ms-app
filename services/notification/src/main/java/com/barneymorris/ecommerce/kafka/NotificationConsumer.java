package com.barneymorris.ecommerce.kafka;

import com.barneymorris.ecommerce.email.EmailService;
import com.barneymorris.ecommerce.kafka.order.OrderConfirmation;
import com.barneymorris.ecommerce.kafka.payment.PaymentConfirmation;
import com.barneymorris.ecommerce.notification.Notification;
import com.barneymorris.ecommerce.notification.NotificationRepository;
import com.barneymorris.ecommerce.notification.NotificationType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotification(
            PaymentConfirmation paymentConfirmation
    ) throws MessagingException {
        log.info(String.format("Consuming the message from payment-topic Topic:: %s", paymentConfirmation);

        notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );

        var customerName = paymentConfirmation.customerFirstname() + " " + paymentConfirmation.customerLastname();
        emailService.sentPaymentSuccessEmail(paymentConfirmation.customerEmail(), customerName, paymentConfirmation.amount(), paymentConfirmation.orderReference());
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotification(
            OrderConfirmation orderConfirmation
    ) throws MessagingException {
        log.info(String.format("Consuming the message from order-topic Topic:: %s", orderConfirmation));

        notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );

        var customerName = orderConfirmation.customer().firstname() + " " + orderConfirmation.customer().lastname();
        emailService.sentOrderConfirmationEmail(orderConfirmation.customer().email(), customerName, orderConfirmation.totalAmount(), orderConfirmation.orderReference(), orderConfirmation.products());
    }
}
