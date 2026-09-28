package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Order;
import application.domain.ports.out.NotificationPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PaymentGatewayPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderCheckoutServiceTest {

    private OrderCheckoutService checkoutService;
    private InMemoryOrderRepository orderRepository;
    private MockPaymentGateway paymentGateway;
    private MockNotificationPort notificationPort;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        paymentGateway = new MockPaymentGateway();
        notificationPort = new MockNotificationPort();
        checkoutService = new OrderCheckoutService(orderRepository, paymentGateway, notificationPort);
    }

    @Test
    @DisplayName("Should successfully checkout cart, process payment, and notify buyer")
    void shouldCheckoutCartAndPay() {
        Cart cart = new Cart("CART-1", "BUY-1");
        cart.addItem("PROD-1", new Money(100.00), 2);
        cart.addItem("PROD-2", new Money(50.00), 1);
        orderRepository.saveCart(cart);

        Address shipping = new Address("123 Street", "City", "State", "12345", "USA");
        Order order = checkoutService.checkoutCart("BUY-1", "ORD-101", shipping);

        assertNotNull(order);
        assertEquals("ORD-101", order.getOrderId());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(new Money(250.00), order.getTotalAmount());
        assertTrue(cart.getItems().isEmpty());

        boolean paid = checkoutService.processOrderPayment("ORD-101", "tok_test123", new Email("buyer@nexusmarket.com"));
        assertTrue(paid);
        assertEquals(OrderStatus.PAID, orderRepository.findOrderById("ORD-101").get().getStatus());
        assertEquals(1, notificationPort.sentEmails.size());
    }

    @Test
    @DisplayName("Should reject checkout when shopping cart is empty")
    void shouldRejectEmptyCartCheckout() {
        Cart cart = new Cart("CART-1", "BUY-1");
        orderRepository.saveCart(cart);

        Address shipping = new Address("123 Street", "City", "State", "12345", "USA");
        assertThrows(InvalidDomainStateException.class, () ->
                checkoutService.checkoutCart("BUY-1", "ORD-101", shipping)
        );
    }

    private static class InMemoryOrderRepository implements OrderRepositoryPort {
        private final Map<String, Cart> carts = new HashMap<>();
        private final Map<String, Order> orders = new HashMap<>();

        @Override
        public Optional<Cart> findCartByBuyerId(String buyerId) {
            return Optional.ofNullable(carts.get(buyerId));
        }

        @Override
        public Cart saveCart(Cart cart) {
            carts.put(cart.getBuyerId(), cart);
            return cart;
        }

        @Override
        public Optional<Order> findOrderById(String orderId) {
            return Optional.ofNullable(orders.get(orderId));
        }

        @Override
        public List<Order> findOrdersByBuyerId(String buyerId) {
            return orders.values().stream().filter(o -> o.getBuyerId().equals(buyerId)).toList();
        }

        @Override
        public Order saveOrder(Order order) {
            orders.put(order.getOrderId(), order);
            return order;
        }
    }

    private static class MockPaymentGateway implements PaymentGatewayPort {
        @Override
        public boolean processPayment(String orderId, Money amount, String paymentToken) {
            return true;
        }

        @Override
        public boolean processRefund(String refundId, Money amount) {
            return true;
        }
    }

    private static class MockNotificationPort implements NotificationPort {
        final List<String> sentEmails = new ArrayList<>();

        @Override
        public void sendEmailNotification(Email recipient, String subject, String body) {
            sentEmails.add(subject + " -> " + recipient.getValue());
        }
    }
}
