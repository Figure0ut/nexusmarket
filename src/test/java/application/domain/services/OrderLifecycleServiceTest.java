package application.domain.services;

import application.domain.enums.InventoryMovementType;
import application.domain.enums.OrderStatus;
import application.domain.enums.ShipmentStatus;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.valueobjects.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderLifecycleServiceTest {

    private final MarketplaceFixture f = new MarketplaceFixture().withCatalog();

    private Inventory laptopStock() {
        return f.inventories.findByProductAndWarehouse("P-LAPTOP", "WH-SEL-1").orElseThrow();
    }

    @Test
    @DisplayName("Full physical flow: cart -> pending payment (reserve) -> paid (invoice) -> dispatched (sale outflow) -> delivered")
    void physicalOrderLifecycle() {
        f.addItemToCart.addItemToCart("BUY-1", "P-LAPTOP", 2);
        Order order = f.checkout.checkoutCart("BUY-1", "ORD-1", new Address("Main Street 1"));
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(8, laptopStock().getAvailableStock().getValue());
        assertEquals(2, laptopStock().getReservedStock().getValue());
        assertEquals(InventoryMovementType.RESERVE, laptopStock().getLastMovementType());
        assertTrue(f.getOrCreateCart.getOrCreateCart("BUY-1").isEmpty());

        Order paid = f.pay.processOrderPayment("ORD-1", "tok_visa", f.buyer);
        assertEquals(OrderStatus.PAID, paid.getStatus());
        assertTrue(f.invoices.findByOrderId("ORD-1").isPresent());
        assertEquals(1, f.sentEmails.size());

        Shipment shipment = f.prepareShipment.prepareShipment("SHP-1", "ORD-1");
        assertEquals("WH-SEL-1", shipment.getWarehouseId());
        f.dispatch.dispatchShipment("SHP-1", "DHL", "TRK-1");
        assertEquals(OrderStatus.DISPATCHED, f.orders.findOrderById("ORD-1").orElseThrow().getStatus());
        assertEquals(0, laptopStock().getReservedStock().getValue());
        assertEquals(InventoryMovementType.OUTFLOW_SALE, laptopStock().getLastMovementType());

        Shipment delivered = f.confirmDelivery.confirmDelivery("SHP-1");
        assertEquals(ShipmentStatus.DELIVERED, delivered.getStatus());
        assertEquals(OrderStatus.DELIVERED_FINALIZED, f.orders.findOrderById("ORD-1").orElseThrow().getStatus());
    }

    @Test
    @DisplayName("Digital-only order is finalized right after payment and needs no shipment")
    void digitalOrder() {
        f.addItemToCart.addItemToCart("BUY-1", "P-EBOOK", 1);
        f.checkout.checkoutCart("BUY-1", "ORD-2", new Address("Main Street 1"));
        Order paid = f.pay.processOrderPayment("ORD-2", "tok_visa", f.buyer);
        assertEquals(OrderStatus.DELIVERED_FINALIZED, paid.getStatus());
        assertThrows(InvalidDomainStateException.class, () -> f.prepareShipment.prepareShipment("SHP-2", "ORD-2"));
    }

    @Test
    @DisplayName("Checkout fails without enough non-damaged stock and nothing is reserved")
    void insufficientStock() {
        f.markDamaged.markStockAsDamaged("P-LAPTOP", "WH-SEL-1", 9);
        f.addItemToCart.addItemToCart("BUY-1", "P-EBOOK", 1);
        f.addItemToCart.addItemToCart("BUY-1", "P-LAPTOP", 2);
        assertThrows(InvalidDomainStateException.class, () -> f.checkout.checkoutCart("BUY-1", "ORD-3", new Address("Main Street 1")));
        assertEquals(1, laptopStock().getAvailableStock().getValue());
        assertEquals(0, laptopStock().getReservedStock().getValue());
        assertTrue(f.orders.findOrderById("ORD-3").isEmpty());
    }

    @Test
    @DisplayName("A rejected payment keeps the order pending")
    void rejectedPayment() {
        f.addItemToCart.addItemToCart("BUY-1", "P-EBOOK", 1);
        f.checkout.checkoutCart("BUY-1", "ORD-4", new Address("Main Street 1"));
        f.paymentsApproved = false;
        assertThrows(InvalidDomainStateException.class, () -> f.pay.processOrderPayment("ORD-4", "tok_bad", f.buyer));
        assertEquals(OrderStatus.PENDING_PAYMENT, f.orders.findOrderById("ORD-4").orElseThrow().getStatus());
        assertTrue(f.invoices.findByOrderId("ORD-4").isEmpty());
    }

    @Test
    @DisplayName("Shipments are only prepared for PAID orders")
    void shipmentRequiresPaidOrder() {
        f.addItemToCart.addItemToCart("BUY-1", "P-LAPTOP", 1);
        f.checkout.checkoutCart("BUY-1", "ORD-5", new Address("Main Street 1"));
        assertThrows(InvalidDomainStateException.class, () -> f.prepareShipment.prepareShipment("SHP-5", "ORD-5"));
    }
}
