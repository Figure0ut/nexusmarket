package application.domain.services;

import application.domain.models.AdministrativeReport;
import application.domain.valueobjects.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AdministrativeReportServiceTest {

    @Test
    @DisplayName("The report consolidates users, catalog, inventory, orders and sales")
    void consolidatesInformation() {
        MarketplaceFixture f = new MarketplaceFixture().withCatalog();
        f.addItemToCart.addItemToCart("BUY-1", "P-LAPTOP", 1);
        f.checkout.checkoutCart("BUY-1", "ORD-1", new Address("Main Street 1"));
        f.pay.processOrderPayment("ORD-1", "tok", f.buyer);

        AdministrativeReport report = f.report.generateReport();
        assertEquals(1L, report.getUsersByRole().get("ADMIN"));
        assertEquals(1L, report.getUsersByRole().get("SELLER"));
        assertEquals(1L, report.getUsersByRole().get("BUYER"));
        assertEquals(2L, report.getProductsByStatus().get("PUBLISHED"));
        assertEquals(9, report.getTotalAvailableStock());
        assertEquals(1, report.getTotalReservedStock());
        assertEquals(1L, report.getOrdersByStatus().get("PAID"));
        assertEquals(0, new BigDecimal("1000.00").compareTo(report.getTotalSalesAmount()));
        assertEquals(1, report.getInvoicesIssued());
    }
}
