package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Cart;
import application.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartManagementServiceTest {

    private final MarketplaceFixture f = new MarketplaceFixture().withCatalog();

    @Test
    @DisplayName("The cart is the CART stage of the order and uses catalog prices")
    void addItems() {
        f.addItemToCart.addItemToCart("BUY-1", "P-LAPTOP", 1);
        Cart cart = f.addItemToCart.addItemToCart("BUY-1", "P-LAPTOP", 2);
        assertEquals(OrderStatus.CART, cart.getStatus());
        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(new Money(3000), cart.calculateTotal());
    }

    @Test
    @DisplayName("Only published products can be added to the cart")
    void onlyPublishedProducts() {
        new SuspendProductService(f.products).suspendProduct("P-LAPTOP");
        assertThrows(InvalidDomainStateException.class, () -> f.addItemToCart.addItemToCart("BUY-1", "P-LAPTOP", 1));
    }

    @Test
    @DisplayName("Remove and clear items")
    void removeAndClear() {
        f.addItemToCart.addItemToCart("BUY-1", "P-LAPTOP", 1);
        f.addItemToCart.addItemToCart("BUY-1", "P-EBOOK", 1);
        assertEquals(1, new RemoveItemFromCartService(f.orders).removeItemFromCart("BUY-1", "P-LAPTOP").getItems().size());
        new ClearCartService(f.orders).clearCart("BUY-1");
        assertTrue(f.getOrCreateCart.getOrCreateCart("BUY-1").isEmpty());
    }
}
