package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Atomic domain service responsible exclusively for converting a buyer's cart into a pending order.
 */
public class CheckoutCartService {

    private final OrderRepositoryPort orderRepositoryPort;

    public CheckoutCartService(OrderRepositoryPort orderRepositoryPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
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
}
