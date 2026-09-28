package application.infrastructure.adapters.out.memory;

import application.domain.models.Cart;
import application.domain.models.Order;
import application.domain.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderRepositoryAdapter implements OrderRepositoryPort {

    private final Map<String, Cart> cartStorage = new ConcurrentHashMap<>();
    private final Map<String, Order> orderStorage = new ConcurrentHashMap<>();

    @Override
    public Optional<Cart> findCartByBuyerId(String buyerId) {
        return Optional.ofNullable(cartStorage.get(buyerId));
    }

    @Override
    public Cart saveCart(Cart cart) {
        cartStorage.put(cart.getBuyerId(), cart);
        return cart;
    }

    @Override
    public Optional<Order> findOrderById(String orderId) {
        return Optional.ofNullable(orderStorage.get(orderId));
    }

    @Override
    public List<Order> findOrdersByBuyerId(String buyerId) {
        return orderStorage.values().stream()
                .filter(o -> o.getBuyerId().equals(buyerId))
                .toList();
    }

    @Override
    public Order saveOrder(Order order) {
        orderStorage.put(order.getOrderId(), order);
        return order;
    }
}
