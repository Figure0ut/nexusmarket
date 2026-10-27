package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Order;
import application.domain.ports.out.NotificationPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PaymentGatewayPort;
import application.domain.valueobjects.Email;

/**
 * Atomic domain service responsible exclusively for processing payment for a pending order
 * via the payment gateway and notifying the buyer upon success.
 */
public class ProcessOrderPaymentService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final PaymentGatewayPort paymentGatewayPort;
    private final NotificationPort notificationPort;

    public ProcessOrderPaymentService(OrderRepositoryPort orderRepositoryPort,
                                      PaymentGatewayPort paymentGatewayPort,
                                      NotificationPort notificationPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        if (paymentGatewayPort == null) {
            throw new IllegalArgumentException("PaymentGatewayPort cannot be null.");
        }
        if (notificationPort == null) {
            throw new IllegalArgumentException("NotificationPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
        this.paymentGatewayPort = paymentGatewayPort;
        this.notificationPort = notificationPort;
    }

    public boolean processOrderPayment(String orderId, String paymentToken, Email buyerEmail) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        if (paymentToken == null || paymentToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment token cannot be null or empty.");
        }

        Order order = orderRepositoryPort.findOrderById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidDomainStateException("Order '" + orderId + "' is not awaiting payment. Current status: " + order.getStatus());
        }

        boolean paymentSuccess = paymentGatewayPort.processPayment(orderId, order.getTotalAmount(), paymentToken);
        if (paymentSuccess) {
            order.markAsPaid();
            orderRepositoryPort.saveOrder(order);

            if (buyerEmail != null) {
                notificationPort.sendEmailNotification(
                        buyerEmail,
                        "Order Payment Confirmation: " + orderId,
                        "Your order " + orderId + " with total " + order.getTotalAmount() + " has been paid successfully."
                );
            }
            return true;
        }

        return false;
    }
}
