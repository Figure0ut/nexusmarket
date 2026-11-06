package application.domain.services;

import application.domain.enums.InventoryMovementType;
import application.domain.enums.RefundStatus;
import application.domain.enums.ReturnReason;
import application.domain.enums.ReturnStatus;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Inventory;
import application.domain.models.Refund;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReturnRefundServiceTest {

    private final MarketplaceFixture f = new MarketplaceFixture().withCatalog();

    private void deliveredOrder() {
        f.addItemToCart.addItemToCart("BUY-1", "P-LAPTOP", 2);
        f.checkout.checkoutCart("BUY-1", "ORD-1", new Address("Main Street 1"));
        f.pay.processOrderPayment("ORD-1", "tok", f.buyer);
        f.prepareShipment.prepareShipment("SHP-1", "ORD-1");
        f.dispatch.dispatchShipment("SHP-1", "DHL", "TRK");
        f.confirmDelivery.confirmDelivery("SHP-1");
    }

    @Test
    @DisplayName("Returns can only be requested for delivered orders of the same buyer")
    void returnRequiresDeliveredOrder() {
        f.addItemToCart.addItemToCart("BUY-1", "P-LAPTOP", 1);
        f.checkout.checkoutCart("BUY-1", "ORD-0", new Address("Main Street 1"));
        assertThrows(InvalidDomainStateException.class, () -> f.requestReturn.requestReturn("RET-0", "ORD-0", "BUY-1", "P-LAPTOP", ReturnReason.DEFECTIVE));
        deliveredOrder();
        assertThrows(InvalidDomainStateException.class, () -> f.requestReturn.requestReturn("RET-1", "ORD-1", "BUY-2", "P-LAPTOP", ReturnReason.DEFECTIVE));
        assertThrows(InvalidDomainStateException.class, () -> f.requestReturn.requestReturn("RET-1", "ORD-1", "BUY-1", "P-EBOOK", ReturnReason.DEFECTIVE));
    }

    @Test
    @DisplayName("Approved return: units come back (RETURN movement) and the amount paid is refunded")
    void approvedReturnIsRefunded() {
        deliveredOrder();
        f.requestReturn.requestReturn("RET-1", "ORD-1", "BUY-1", "P-LAPTOP", ReturnReason.DAMAGED);
        f.approveReturn.approveReturn("RET-1");
        Refund refund = f.processRefund.processReturnedItemAndRefund("REF-1", "RET-1", false);

        assertEquals(RefundStatus.PROCESSED, refund.getStatus());
        assertEquals(new Money(2000), refund.getAmount());
        assertEquals(ReturnStatus.ITEM_RECEIVED, f.returns.findReturnById("RET-1").orElseThrow().getStatus());
        Inventory inv = f.inventories.findByProductAndWarehouse("P-LAPTOP", "WH-SEL-1").orElseThrow();
        assertEquals(InventoryMovementType.RETURN, inv.getLastMovementType());
        assertEquals(2, inv.getDamagedStock().getValue());
    }

    @Test
    @DisplayName("A rejected return cannot be refunded")
    void rejectedReturn() {
        deliveredOrder();
        f.requestReturn.requestReturn("RET-1", "ORD-1", "BUY-1", "P-LAPTOP", ReturnReason.CUSTOMER_REGRET);
        f.rejectReturn.rejectReturn("RET-1");
        assertThrows(InvalidDomainStateException.class, () -> f.processRefund.processReturnedItemAndRefund("REF-1", "RET-1", true));
    }
}
