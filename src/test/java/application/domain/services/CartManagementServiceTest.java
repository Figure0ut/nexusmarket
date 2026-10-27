package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Cart;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.Money;
import application.infrastructure.adapters.out.memory.InMemoryOrderRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartManagementServiceTest {

    private OrderRepositoryPort orderRepositoryPort;
    private GetOrCreateCartService getOrCreateCartService;
    private AddItemToCartService addItemToCartService;
    private RemoveItemFromCartService removeItemFromCartService;
    private ClearCartService clearCartService;

    @BeforeEach
    void setUp() {
        orderRepositoryPort = new InMemoryOrderRepositoryAdapter();
        getOrCreateCartService = new GetOrCreateCartService(orderRepositoryPort);
        addItemToCartService = new AddItemToCartService(orderRepositoryPort, getOrCreateCartService);
        removeItemFromCartService = new RemoveItemFromCartService(orderRepositoryPort);
        clearCartService = new ClearCartService(orderRepositoryPort);
    }

    @Test
    @DisplayName("Should create cart if it does not exist and retrieve it")
    void shouldGetOrCreateCart() {
        Cart cart = getOrCreateCartService.getOrCreateCart("BUYER-01");

        assertNotNull(cart);
        assertEquals("BUYER-01", cart.getBuyerId());
        assertEquals("CART-BUYER-01", cart.getCartId());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should add items to cart and increment existing item quantities")
    void shouldAddItemsToCart() {
        addItemToCartService.addItemToCart("BUYER-01", "PROD-100", new Money(25.00), 2);
        Cart cart = addItemToCartService.addItemToCart("BUYER-01", "PROD-100", new Money(25.00), 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
        assertEquals(new Money(125.00), cart.calculateTotal());
    }

    @Test
    @DisplayName("Should remove item from cart")
    void shouldRemoveItemFromCart() {
        addItemToCartService.addItemToCart("BUYER-01", "PROD-100", new Money(20.00), 1);
        addItemToCartService.addItemToCart("BUYER-01", "PROD-200", new Money(30.00), 1);

        Cart cart = removeItemFromCartService.removeItemFromCart("BUYER-01", "PROD-100");

        assertEquals(1, cart.getItems().size());
        assertEquals("PROD-200", cart.getItems().get(0).getProductId());
    }

    @Test
    @DisplayName("Should clear all items from cart")
    void shouldClearCart() {
        addItemToCartService.addItemToCart("BUYER-01", "PROD-100", new Money(10.00), 2);
        clearCartService.clearCart("BUYER-01");

        Cart cart = getOrCreateCartService.getOrCreateCart("BUYER-01");
        assertTrue(cart.getItems().isEmpty());
        assertEquals(Money.zero(), cart.calculateTotal());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when clearing non-existent cart")
    void shouldThrowWhenCartNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                clearCartService.clearCart("UNKNOWN-BUYER")
        );
    }
}
