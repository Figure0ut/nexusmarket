package application.infrastructure.adapters.out.sql;

import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;
import application.infrastructure.adapters.out.sql.entity.CartEntity;
import application.infrastructure.adapters.out.sql.entity.CartItemEntity;
import application.infrastructure.adapters.out.sql.entity.OrderEntity;
import application.infrastructure.adapters.out.sql.entity.OrderItemEntity;
import application.infrastructure.adapters.out.sql.repository.SpringDataJpaCartRepository;
import application.infrastructure.adapters.out.sql.repository.SpringDataJpaOrderRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Primary
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataJpaOrderRepository orderJpaRepository;
    private final SpringDataJpaCartRepository cartJpaRepository;

    public JpaOrderRepositoryAdapter(SpringDataJpaOrderRepository orderJpaRepository,
                                     SpringDataJpaCartRepository cartJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
        this.cartJpaRepository = cartJpaRepository;
    }

    @Override
    public Optional<Cart> findCartByBuyerId(String buyerId) {
        if (buyerId == null) return Optional.empty();
        return cartJpaRepository.findByBuyerId(buyerId).map(this::cartToDomain);
    }

    @Override
    public Cart saveCart(Cart cart) {
        CartEntity entity = cartToEntity(cart);
        CartEntity saved = cartJpaRepository.save(entity);
        return cartToDomain(saved);
    }

    @Override
    public Optional<Order> findOrderById(String orderId) {
        if (orderId == null) return Optional.empty();
        return orderJpaRepository.findById(orderId).map(this::orderToDomain);
    }

    @Override
    public List<Order> findOrdersByBuyerId(String buyerId) {
        if (buyerId == null) return List.of();
        return orderJpaRepository.findByBuyerId(buyerId).stream().map(this::orderToDomain).toList();
    }

    @Override
    public List<Order> findAllOrders() {
        return orderJpaRepository.findAll().stream().map(this::orderToDomain).toList();
    }

    @Override
    public Order saveOrder(Order order) {
        OrderEntity entity = orderToEntity(order);
        OrderEntity saved = orderJpaRepository.save(entity);
        return orderToDomain(saved);
    }

    private Cart cartToDomain(CartEntity entity) {
        if (entity == null) return null;
        Cart cart = new Cart(entity.getCartId(), entity.getBuyerId());
        if (entity.getItems() != null) {
            for (CartItemEntity itemEntity : entity.getItems()) {
                cart.addItem(itemEntity.getProductId(), new Money(itemEntity.getUnitPrice()), itemEntity.getQuantity());
            }
        }
        return cart;
    }

    private CartEntity cartToEntity(Cart cart) {
        if (cart == null) return null;
        CartEntity entity = new CartEntity();
        entity.setCartId(cart.getCartId());
        entity.setBuyerId(cart.getBuyerId());

        List<CartItemEntity> items = new ArrayList<>();
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                items.add(new CartItemEntity(
                        item.getProductId(),
                        item.getUnitPrice() != null ? item.getUnitPrice().getAmount().doubleValue() : 0.0,
                        item.getQuantity()
                ));
            }
        }
        entity.setItems(items);
        return entity;
    }

    private Order orderToDomain(OrderEntity entity) {
        if (entity == null) return null;
        List<OrderItem> items = new ArrayList<>();
        if (entity.getItems() != null) {
            for (OrderItemEntity itemEntity : entity.getItems()) {
                items.add(new OrderItem(
                        itemEntity.getProductId(),
                        itemEntity.getProductName(),
                        itemEntity.getProductType(),
                        itemEntity.getWarehouseId(),
                        new Money(itemEntity.getUnitPrice()),
                        itemEntity.getQuantity()
                ));
            }
        }
        return new Order(
                entity.getOrderId(),
                entity.getBuyerId(),
                items,
                entity.getStatus(),
                new Address(entity.getShippingAddress())
        );
    }

    private OrderEntity orderToEntity(Order order) {
        if (order == null) return null;
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(order.getOrderId());
        entity.setBuyerId(order.getBuyerId());
        entity.setStatus(order.getStatus());
        entity.setShippingAddress(order.getShippingAddress() != null ? order.getShippingAddress().getStreet() : "");
        entity.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount().getAmount().doubleValue() : 0.0);

        List<OrderItemEntity> items = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                items.add(new OrderItemEntity(
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductType(),
                        item.getWarehouseId(),
                        item.getUnitPrice() != null ? item.getUnitPrice().getAmount().doubleValue() : 0.0,
                        item.getQuantity()
                ));
            }
        }
        entity.setItems(items);
        return entity;
    }
}
