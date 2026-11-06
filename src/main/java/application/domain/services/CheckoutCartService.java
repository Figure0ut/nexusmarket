package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Atomic domain service responsible exclusively for confirming a buyer's cart as an order
 * (CART -> PENDING_PAYMENT, section 6.1 step 5).
 * <p>
 * For each physical item the stock is RESERVED in one warehouse that has enough available stock.
 * Inexistent or damaged inventory can never be reserved (section 11) and stock never becomes negative (Domain 6).
 * Digital items do not reserve inventory.
 */
public class CheckoutCartService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public CheckoutCartService(OrderRepositoryPort orderRepositoryPort,
                               ProductRepositoryPort productRepositoryPort,
                               InventoryRepositoryPort inventoryRepositoryPort,
                               WarehouseRepositoryPort warehouseRepositoryPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
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
        if (orderRepositoryPort.findOrderById(orderId.trim()).isPresent()) {
            throw new application.domain.exceptions.DomainException("Order with ID '" + orderId + "' already exists.");
        }
        Cart cart = orderRepositoryPort.findCartByBuyerId(buyerId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found for buyer: " + buyerId));
        if (cart.isEmpty()) {
            throw new InvalidDomainStateException("Cannot checkout an empty shopping cart for buyer: " + buyerId);
        }

        // 1) Validate every item and choose the warehouse BEFORE touching any stock (all or nothing).
        List<OrderItem> orderItems = new ArrayList<>();
        List<Inventory> inventoriesToReserve = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepositoryPort.findById(cartItem.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + cartItem.getProductId()));
            if (!product.isPublished()) {
                throw new InvalidDomainStateException("Product '" + product.getProductId() + "' is no longer PUBLISHED.");
            }
            String warehouseId = null;
            if (product.isPhysical()) {
                Inventory inventory = findInventoryWithAvailableStock(product.getProductId(), cartItem.getQuantity());
                inventoriesToReserve.add(inventory);
                warehouseId = inventory.getWarehouseId();
            }
            orderItems.add(new OrderItem(product.getProductId(), product.getName(), product.getProductType(),
                    warehouseId, cartItem.getUnitPrice(), cartItem.getQuantity()));
        }

        // 2) Reserve the stock (RESERVE movement).
        for (OrderItem item : orderItems) {
            if (!item.isPhysical()) {
                continue;
            }
            Inventory inventory = inventoriesToReserve.stream()
                    .filter(inv -> inv.getProductId().equals(item.getProductId()))
                    .findFirst().orElseThrow();
            inventory.reserveStock(item.getQuantity());
            inventoryRepositoryPort.save(inventory);
        }

        // 3) Create the order and empty the cart.
        Order order = new Order(orderId.trim(), buyerId.trim(), orderItems, OrderStatus.PENDING_PAYMENT, shippingAddress);
        cart.clear();
        orderRepositoryPort.saveCart(cart);
        return orderRepositoryPort.saveOrder(order);
    }

    private Inventory findInventoryWithAvailableStock(String productId, int quantity) {
        return inventoryRepositoryPort.findByProductId(productId).stream()
                .filter(inv -> inv.getAvailableStock().getValue() >= quantity)
                .filter(inv -> warehouseRepositoryPort.findById(inv.getWarehouseId()).map(w -> w.isActive()).orElse(false))
                .findFirst()
                .orElseThrow(() -> new InvalidDomainStateException("Insufficient available stock for product '" + productId +
                        "' (requested " + quantity + "). Inexistent or damaged inventory cannot be reserved."));
    }
}
