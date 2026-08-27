package com.nexusmarket.domain.model.inventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    @DisplayName("Should successfully reserve stock and confirm sale")
    void shouldReserveAndConfirmSale() {
        Inventory inventory = new Inventory("INV-001", "PROD-001", "WH-001", 100);

        inventory.reserveStock(20);
        assertEquals(80, inventory.getAvailableStock().getValue());
        assertEquals(20, inventory.getReservedStock().getValue());

        inventory.confirmSale(20);
        assertEquals(80, inventory.getAvailableStock().getValue());
        assertEquals(0, inventory.getReservedStock().getValue());
    }

    @Test
    @DisplayName("Should reject reservation when requested stock exceeds available quantity")
    void shouldRejectExcessiveReservation() {
        Inventory inventory = new Inventory("INV-001", "PROD-001", "WH-001", 10);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> inventory.reserveStock(15));
        assertTrue(ex.getMessage().contains("insufficient available stock"));
    }
}
