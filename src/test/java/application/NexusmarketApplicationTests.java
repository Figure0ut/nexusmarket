package application;

import application.domain.services.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class NexusmarketApplicationTests {

    @Autowired private UserAuthenticationService userAuthenticationService;
    @Autowired private SellerIncorporationService sellerIncorporationService;
    @Autowired private CatalogManagementService catalogManagementService;
    @Autowired private WarehouseManagementService warehouseManagementService;
    @Autowired private InventoryAllocationService inventoryAllocationService;
    @Autowired private OrderCheckoutService orderCheckoutService;
    @Autowired private BillingInvoicingService billingInvoicingService;
    @Autowired private LogisticsDispatchService logisticsDispatchService;
    @Autowired private ReturnRefundService returnRefundService;
    @Autowired private OperationAuditService operationAuditService;
    @Autowired private AuthorizationService authorizationService;
    @Autowired private CartManagementService cartManagementService;

    @Test
    @DisplayName("Verify Spring Boot context successfully injects all 12 pure domain services as beans")
    void contextLoadsAndServicesAreInjected() {
        assertNotNull(userAuthenticationService);
        assertNotNull(sellerIncorporationService);
        assertNotNull(catalogManagementService);
        assertNotNull(warehouseManagementService);
        assertNotNull(inventoryAllocationService);
        assertNotNull(orderCheckoutService);
        assertNotNull(billingInvoicingService);
        assertNotNull(logisticsDispatchService);
        assertNotNull(returnRefundService);
        assertNotNull(operationAuditService);
        assertNotNull(authorizationService);
        assertNotNull(cartManagementService);
    }
}
