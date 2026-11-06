package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.NotificationPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PaymentGatewayPort;

/**
 * Atomic domain service responsible exclusively for validating the payment of a pending order
 * (section 6.1, step 6 "Transaction"). On success the order becomes PAID (or DELIVERED_FINALIZED
 * when it only contains digital products), the invoice is issued and the buyer is notified.
 */
public class ProcessOrderPaymentService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final PaymentGatewayPort paymentGatewayPort;
    private final NotificationPort notificationPort;
    private final CreateInvoiceService createInvoiceService;

    public ProcessOrderPaymentService(OrderRepositoryPort orderRepositoryPort,
                                      PaymentGatewayPort paymentGatewayPort,
                                      NotificationPort notificationPort,
                                      CreateInvoiceService createInvoiceService) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        if (paymentGatewayPort == null) {
            throw new IllegalArgumentException("PaymentGatewayPort cannot be null.");
        }
        if (notificationPort == null) {
            throw new IllegalArgumentException("NotificationPort cannot be null.");
        }
        if (createInvoiceService == null) {
            throw new IllegalArgumentException("CreateInvoiceService cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
        this.paymentGatewayPort = paymentGatewayPort;
        this.notificationPort = notificationPort;
        this.createInvoiceService = createInvoiceService;
    }

    /**
     * @param orderId      order to pay
     * @param paymentToken token given by the payment method
     * @param buyer        the buyer that owns the order
     * @return the updated order
     */
    public Order processOrderPayment(String orderId, String paymentToken, User buyer) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        if (paymentToken == null || paymentToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment token cannot be null or empty.");
        }
        if (buyer == null) {
            throw new IllegalArgumentException("Buyer cannot be null.");
        }
        Order order = orderRepositoryPort.findOrderById(orderId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidDomainStateException("Order '" + orderId + "' is not awaiting payment. Current status: " + order.getStatus());
        }
        boolean paymentSuccess = paymentGatewayPort.processPayment(order.getOrderId(), order.getTotalAmount(), paymentToken);
        if (!paymentSuccess) {
            throw new InvalidDomainStateException("Payment for order '" + orderId + "' was rejected by the payment gateway.");
        }
        order.markAsPaid();
        Order saved = orderRepositoryPort.saveOrder(order);
        createInvoiceService.createInvoice(saved.getOrderId(), buyer.getIdentityDocument());
        notificationPort.sendEmailNotification(
                buyer.getEmail(),
                "Order Payment Confirmation: " + saved.getOrderId(),
                "Your order " + saved.getOrderId() + " with total " + saved.getTotalAmount()
                        + " has been paid successfully. Current status: " + saved.getStatus() + "."
        );
        return saved;
    }
}
