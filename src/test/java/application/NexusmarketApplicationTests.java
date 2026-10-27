package application;

import application.domain.services.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class NexusmarketApplicationTests {

    // --- Invoicing / Billing ---
    @Autowired private CreateInvoiceService createInvoiceService;
    @Autowired private GetInvoiceService getInvoiceService;
    @Autowired private MarkInvoicePaidService markInvoicePaidService;
    @Autowired private CancelInvoiceService cancelInvoiceService;

    // --- Cart ---
    @Autowired private GetOrCreateCartService getOrCreateCartService;
    @Autowired private AddItemToCartService addItemToCartService;
    @Autowired private RemoveItemFromCartService removeItemFromCartService;
    @Autowired private ClearCartService clearCartService;

    // --- Order / Checkout ---
    @Autowired private CheckoutCartService checkoutCartService;
    @Autowired private ProcessOrderPaymentService processOrderPaymentService;
    @Autowired private CancelOrderService cancelOrderService;
    @Autowired private GetOrderService getOrderService;
    @Autowired private GetOrdersByBuyerService getOrdersByBuyerService;

    // --- Catalog Management ---
    @Autowired private RegisterProductService registerProductService;
    @Autowired private GetProductService getProductService;
    @Autowired private PublishProductService publishProductService;
    @Autowired private SuspendProductService suspendProductService;
    @Autowired private DiscontinueProductService discontinueProductService;
    @Autowired private UpdateProductPriceService updateProductPriceService;
    @Autowired private AddProductVariantService addProductVariantService;

    // --- Inventory & Stock ---
    @Autowired private GetInventoryService getInventoryService;
    @Autowired private AddStockService addStockService;
    @Autowired private ReserveStockService reserveStockService;
    @Autowired private ReleaseStockReservationService releaseStockReservationService;
    @Autowired private ConfirmSaleStockService confirmSaleStockService;
    @Autowired private MarkStockDamagedService markStockDamagedService;
    @Autowired private GetTotalAvailableStockService getTotalAvailableStockService;

    // --- Logistics & Dispatch ---
    @Autowired private PrepareShipmentService prepareShipmentService;
    @Autowired private GetShipmentService getShipmentService;
    @Autowired private DispatchShipmentService dispatchShipmentService;
    @Autowired private ConfirmDeliveryService confirmDeliveryService;
    @Autowired private ReportFailedDeliveryService reportFailedDeliveryService;

    // --- Return & Refund ---
    @Autowired private RequestReturnService requestReturnService;
    @Autowired private GetReturnRequestService getReturnRequestService;
    @Autowired private ApproveReturnService approveReturnService;
    @Autowired private RejectReturnService rejectReturnService;
    @Autowired private ProcessReturnAndRefundService processReturnAndRefundService;

    // --- Seller Incorporation ---
    @Autowired private IncorporateSellerService incorporateSellerService;

    // --- User & Authentication ---
    @Autowired private AuthenticateUserService authenticateUserService;
    @Autowired private RegisterUserService registerUserService;
    @Autowired private BlockUserService blockUserService;
    @Autowired private ActivateUserService activateUserService;
    @Autowired private GetUserService getUserService;

    // --- Warehouse Management ---
    @Autowired private RegisterWarehouseService registerWarehouseService;
    @Autowired private GetWarehouseService getWarehouseService;
    @Autowired private ActivateWarehouseService activateWarehouseService;
    @Autowired private DeactivateWarehouseService deactivateWarehouseService;
    @Autowired private UpdateWarehouseLocationService updateWarehouseLocationService;
    @Autowired private GetWarehousesByOwnerService getWarehousesByOwnerService;

    // --- Operation Audit ---
    @Autowired private LogOperationService logOperationService;
    @Autowired private GetAllAuditLogsService getAllAuditLogsService;
    @Autowired private GetAuditLogsByAggregateService getAuditLogsByAggregateService;

    // --- Authorization ---
    @Autowired private ValidateAuthorizationService validateAuthorizationService;

    @Test
    @DisplayName("Verify Spring Boot context successfully injects all 53 pure atomic domain services as beans")
    void contextLoadsAndServicesAreInjected() {
        assertNotNull(createInvoiceService);
        assertNotNull(getInvoiceService);
        assertNotNull(markInvoicePaidService);
        assertNotNull(cancelInvoiceService);

        assertNotNull(getOrCreateCartService);
        assertNotNull(addItemToCartService);
        assertNotNull(removeItemFromCartService);
        assertNotNull(clearCartService);

        assertNotNull(checkoutCartService);
        assertNotNull(processOrderPaymentService);
        assertNotNull(cancelOrderService);
        assertNotNull(getOrderService);
        assertNotNull(getOrdersByBuyerService);

        assertNotNull(registerProductService);
        assertNotNull(getProductService);
        assertNotNull(publishProductService);
        assertNotNull(suspendProductService);
        assertNotNull(discontinueProductService);
        assertNotNull(updateProductPriceService);
        assertNotNull(addProductVariantService);

        assertNotNull(getInventoryService);
        assertNotNull(addStockService);
        assertNotNull(reserveStockService);
        assertNotNull(releaseStockReservationService);
        assertNotNull(confirmSaleStockService);
        assertNotNull(markStockDamagedService);
        assertNotNull(getTotalAvailableStockService);

        assertNotNull(prepareShipmentService);
        assertNotNull(getShipmentService);
        assertNotNull(dispatchShipmentService);
        assertNotNull(confirmDeliveryService);
        assertNotNull(reportFailedDeliveryService);

        assertNotNull(requestReturnService);
        assertNotNull(getReturnRequestService);
        assertNotNull(approveReturnService);
        assertNotNull(rejectReturnService);
        assertNotNull(processReturnAndRefundService);

        assertNotNull(incorporateSellerService);

        assertNotNull(authenticateUserService);
        assertNotNull(registerUserService);
        assertNotNull(blockUserService);
        assertNotNull(activateUserService);
        assertNotNull(getUserService);

        assertNotNull(registerWarehouseService);
        assertNotNull(getWarehouseService);
        assertNotNull(activateWarehouseService);
        assertNotNull(deactivateWarehouseService);
        assertNotNull(updateWarehouseLocationService);
        assertNotNull(getWarehousesByOwnerService);

        assertNotNull(logOperationService);
        assertNotNull(getAllAuditLogsService);
        assertNotNull(getAuditLogsByAggregateService);

        assertNotNull(validateAuthorizationService);
    }
}
