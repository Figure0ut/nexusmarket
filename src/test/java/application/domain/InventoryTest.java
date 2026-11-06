package application.domain;

import application.domain.enums.InventoryMovementType;
import application.domain.models.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    @DisplayName("Movements: inflow, reserve, outflow by sale, adjustment and return")
    void movements() {
        Inventory inv = new Inventory("INV-1", "P-1", "W-1", 10);
        inv.addStock(5);
        assertEquals(InventoryMovementType.INFLOW, inv.getLastMovementType());
        inv.reserveStock(4);
        assertEquals(InventoryMovementType.RESERVE, inv.getLastMovementType());
        assertEquals(11, inv.getAvailableStock().getValue());
        assertEquals(4, inv.getReservedStock().getValue());
        inv.confirmSale(4);
        assertEquals(InventoryMovementType.OUTFLOW_SALE, inv.getLastMovementType());
        assertEquals(0, inv.getReservedStock().getValue());
        inv.markAsDamaged(1);
        assertEquals(InventoryMovementType.ADJUSTMENT, inv.getLastMovementType());
        assertEquals(10, inv.getAvailableStock().getValue());
        inv.registerReturn(2, true);
        inv.registerReturn(1, false);
        assertEquals(InventoryMovementType.RETURN, inv.getLastMovementType());
        assertEquals(12, inv.getAvailableStock().getValue());
        assertEquals(2, inv.getDamagedStock().getValue());
    }

    @Test
    @DisplayName("Negative stock is never allowed")
    void noNegativeStock() {
        Inventory inv = new Inventory("INV-1", "P-1", "W-1", 3);
        assertThrows(IllegalStateException.class, () -> inv.reserveStock(4));
        assertThrows(IllegalStateException.class, () -> inv.markAsDamaged(4));
        assertThrows(IllegalStateException.class, () -> inv.confirmSale(1));
        assertEquals(3, inv.getAvailableStock().getValue());
    }

    @Test
    @DisplayName("Damaged stock cannot be reserved")
    void damagedStockCannotBeReserved() {
        Inventory inv = new Inventory("INV-1", "P-1", "W-1", 5);
        inv.markAsDamaged(5);
        assertEquals(5, inv.getDamagedStock().getValue());
        assertThrows(IllegalStateException.class, () -> inv.reserveStock(1));
    }
}
