package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.ports.out.NotificationPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PaymentGatewayPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;

import java.util.ArrayList;
import java.util.List;

public class OrderCheckoutService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final PaymentGatewayPort paymentGatewayPort;
    private final NotificationPort notificationPort;

    public OrderCheckoutService(OrderRepositoryPort orderRepositoryPort,
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

    public Order checkoutCart(String buyerId, String orderId, Address shippingAddress) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address cannot be null.");
        }

        Cart cart = orderRepositoryPort.findCartByBuyerId(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found for buyer: " + buyerId));

        if (cart.getItems().isEmpty()) {
            throw new InvalidDomainStateException("Cannot checkout an empty shopping cart for buyer: " + buyerId);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            orderItems.add(new OrderItem(
                    cartItem.getProductId(),
                    "Product " + cartItem.getProductId(),
                    cartItem.getUnitPrice(),
                    cartItem.getQuantity()
            ));
        }

        Order order = new Order(orderId, buyerId, orderItems, OrderStatus.PENDING_PAYMENT, shippingAddress);

        cart.clear();
        orderRepositoryPort.saveCart(cart);

        return orderRepositoryPort.saveOrder(order);
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

    public void cancelOrder(String orderId) {
        Order order = orderRepositoryPort.findOrderById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        order.cancel();
        orderRepositoryPort.saveOrder(order);
    }
}
