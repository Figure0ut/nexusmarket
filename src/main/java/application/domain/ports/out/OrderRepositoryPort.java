package application.domain.ports.out;

import application.domain.models.Cart;
import application.domain.models.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Optional<Cart> findCartByBuyerId(String buyerId);
    Cart saveCart(Cart cart);
    Optional<Order> findOrderById(String orderId);
    List<Order> findOrdersByBuyerId(String buyerId);
    Order saveOrder(Order order);
}
