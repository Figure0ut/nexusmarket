package application.infrastructure.config;

import application.domain.ports.out.*;
import application.domain.services.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class wiring all 53 atomic domain services as pure Java beans,
 * injecting required outbound persistence and gateway port adapters.
 */
@Configuration
public class DomainServicesConfig {

    // --- Invoicing / Billing ---

    @Bean
    public CreateInvoiceService createInvoiceService(InvoiceRepositoryPort invoiceRepositoryPort, OrderRepositoryPort orderRepositoryPort) {
        return new CreateInvoiceService(invoiceRepositoryPort, orderRepositoryPort);
    }

    @Bean
    public GetInvoiceService getInvoiceService(InvoiceRepositoryPort invoiceRepositoryPort) {
        return new GetInvoiceService(invoiceRepositoryPort);
    }

    @Bean
    public MarkInvoicePaidService markInvoicePaidService(InvoiceRepositoryPort invoiceRepositoryPort) {
        return new MarkInvoicePaidService(invoiceRepositoryPort);
    }

    @Bean
    public CancelInvoiceService cancelInvoiceService(InvoiceRepositoryPort invoiceRepositoryPort) {
        return new CancelInvoiceService(invoiceRepositoryPort);
    }

    // --- Cart ---

    @Bean
    public GetOrCreateCartService getOrCreateCartService(OrderRepositoryPort orderRepositoryPort) {
        return new GetOrCreateCartService(orderRepositoryPort);
    }

    @Bean
    public AddItemToCartService addItemToCartService(OrderRepositoryPort orderRepositoryPort, GetOrCreateCartService getOrCreateCartService) {
        return new AddItemToCartService(orderRepositoryPort, getOrCreateCartService);
    }

    @Bean
    public RemoveItemFromCartService removeItemFromCartService(OrderRepositoryPort orderRepositoryPort) {
        return new RemoveItemFromCartService(orderRepositoryPort);
    }

    @Bean
    public ClearCartService clearCartService(OrderRepositoryPort orderRepositoryPort) {
        return new ClearCartService(orderRepositoryPort);
    }

    // --- Order / Checkout ---

    @Bean
    public CheckoutCartService checkoutCartService(OrderRepositoryPort orderRepositoryPort) {
        return new CheckoutCartService(orderRepositoryPort);
    }

    @Bean
    public ProcessOrderPaymentService processOrderPaymentService(OrderRepositoryPort orderRepositoryPort,
                                                                PaymentGatewayPort paymentGatewayPort,
                                                                NotificationPort notificationPort) {
        return new ProcessOrderPaymentService(orderRepositoryPort, paymentGatewayPort, notificationPort);
    }

    @Bean
    public CancelOrderService cancelOrderService(OrderRepositoryPort orderRepositoryPort) {
        return new CancelOrderService(orderRepositoryPort);
    }

    @Bean
    public GetOrderService getOrderService(OrderRepositoryPort orderRepositoryPort) {
        return new GetOrderService(orderRepositoryPort);
    }

    @Bean
    public GetOrdersByBuyerService getOrdersByBuyerService(OrderRepositoryPort orderRepositoryPort) {
        return new GetOrdersByBuyerService(orderRepositoryPort);
    }

    // --- Catalog Management ---

    @Bean
    public RegisterProductService registerProductService(ProductRepositoryPort productRepositoryPort) {
        return new RegisterProductService(productRepositoryPort);
    }

    @Bean
    public GetProductService getProductService(ProductRepositoryPort productRepositoryPort) {
        return new GetProductService(productRepositoryPort);
    }

    @Bean
    public PublishProductService publishProductService(ProductRepositoryPort productRepositoryPort) {
        return new PublishProductService(productRepositoryPort);
    }

    @Bean
    public SuspendProductService suspendProductService(ProductRepositoryPort productRepositoryPort) {
        return new SuspendProductService(productRepositoryPort);
    }

    @Bean
    public DiscontinueProductService discontinueProductService(ProductRepositoryPort productRepositoryPort) {
        return new DiscontinueProductService(productRepositoryPort);
    }

    @Bean
    public UpdateProductPriceService updateProductPriceService(ProductRepositoryPort productRepositoryPort) {
        return new UpdateProductPriceService(productRepositoryPort);
    }

    @Bean
    public AddProductVariantService addProductVariantService(ProductRepositoryPort productRepositoryPort) {
        return new AddProductVariantService(productRepositoryPort);
    }

    // --- Inventory & Stock ---

    @Bean
    public GetInventoryService getInventoryService(InventoryRepositoryPort inventoryRepositoryPort) {
        return new GetInventoryService(inventoryRepositoryPort);
    }

    @Bean
    public AddStockService addStockService(InventoryRepositoryPort inventoryRepositoryPort,
                                           WarehouseRepositoryPort warehouseRepositoryPort) {
        return new AddStockService(inventoryRepositoryPort, warehouseRepositoryPort);
    }

    @Bean
    public ReserveStockService reserveStockService(InventoryRepositoryPort inventoryRepositoryPort) {
        return new ReserveStockService(inventoryRepositoryPort);
    }

    @Bean
    public ReleaseStockReservationService releaseStockReservationService(InventoryRepositoryPort inventoryRepositoryPort) {
        return new ReleaseStockReservationService(inventoryRepositoryPort);
    }

    @Bean
    public ConfirmSaleStockService confirmSaleStockService(InventoryRepositoryPort inventoryRepositoryPort) {
        return new ConfirmSaleStockService(inventoryRepositoryPort);
    }

    @Bean
    public MarkStockDamagedService markStockDamagedService(InventoryRepositoryPort inventoryRepositoryPort) {
        return new MarkStockDamagedService(inventoryRepositoryPort);
    }

    @Bean
    public GetTotalAvailableStockService getTotalAvailableStockService(InventoryRepositoryPort inventoryRepositoryPort) {
        return new GetTotalAvailableStockService(inventoryRepositoryPort);
    }

    // --- Logistics & Dispatch ---

    @Bean
    public PrepareShipmentService prepareShipmentService(ShipmentRepositoryPort shipmentRepositoryPort,
                                                         OrderRepositoryPort orderRepositoryPort,
                                                         WarehouseRepositoryPort warehouseRepositoryPort) {
        return new PrepareShipmentService(shipmentRepositoryPort, orderRepositoryPort, warehouseRepositoryPort);
    }

    @Bean
    public GetShipmentService getShipmentService(ShipmentRepositoryPort shipmentRepositoryPort) {
        return new GetShipmentService(shipmentRepositoryPort);
    }

    @Bean
    public DispatchShipmentService dispatchShipmentService(ShipmentRepositoryPort shipmentRepositoryPort,
                                                           OrderRepositoryPort orderRepositoryPort) {
        return new DispatchShipmentService(shipmentRepositoryPort, orderRepositoryPort);
    }

    @Bean
    public ConfirmDeliveryService confirmDeliveryService(ShipmentRepositoryPort shipmentRepositoryPort,
                                                         OrderRepositoryPort orderRepositoryPort) {
        return new ConfirmDeliveryService(shipmentRepositoryPort, orderRepositoryPort);
    }

    @Bean
    public ReportFailedDeliveryService reportFailedDeliveryService(ShipmentRepositoryPort shipmentRepositoryPort) {
        return new ReportFailedDeliveryService(shipmentRepositoryPort);
    }

    // --- Return & Refund ---

    @Bean
    public RequestReturnService requestReturnService(ReturnRepositoryPort returnRepositoryPort,
                                                     OrderRepositoryPort orderRepositoryPort) {
        return new RequestReturnService(returnRepositoryPort, orderRepositoryPort);
    }

    @Bean
    public GetReturnRequestService getReturnRequestService(ReturnRepositoryPort returnRepositoryPort) {
        return new GetReturnRequestService(returnRepositoryPort);
    }

    @Bean
    public ApproveReturnService approveReturnService(ReturnRepositoryPort returnRepositoryPort) {
        return new ApproveReturnService(returnRepositoryPort);
    }

    @Bean
    public RejectReturnService rejectReturnService(ReturnRepositoryPort returnRepositoryPort) {
        return new RejectReturnService(returnRepositoryPort);
    }

    @Bean
    public ProcessReturnAndRefundService processReturnAndRefundService(ReturnRepositoryPort returnRepositoryPort,
                                                                       InventoryRepositoryPort inventoryRepositoryPort,
                                                                       PaymentGatewayPort paymentGatewayPort) {
        return new ProcessReturnAndRefundService(returnRepositoryPort, inventoryRepositoryPort, paymentGatewayPort);
    }

    // --- Seller Incorporation ---

    @Bean
    public IncorporateSellerService incorporateSellerService(UserRepositoryPort userRepositoryPort) {
        return new IncorporateSellerService(userRepositoryPort);
    }

    // --- User & Authentication ---

    @Bean
    public AuthenticateUserService authenticateUserService(UserRepositoryPort userRepositoryPort) {
        return new AuthenticateUserService(userRepositoryPort);
    }

    @Bean
    public RegisterUserService registerUserService(UserRepositoryPort userRepositoryPort) {
        return new RegisterUserService(userRepositoryPort);
    }

    @Bean
    public BlockUserService blockUserService(UserRepositoryPort userRepositoryPort) {
        return new BlockUserService(userRepositoryPort);
    }

    @Bean
    public ActivateUserService activateUserService(UserRepositoryPort userRepositoryPort) {
        return new ActivateUserService(userRepositoryPort);
    }

    @Bean
    public GetUserService getUserService(UserRepositoryPort userRepositoryPort) {
        return new GetUserService(userRepositoryPort);
    }

    // --- Warehouse Management ---

    @Bean
    public RegisterWarehouseService registerWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new RegisterWarehouseService(warehouseRepositoryPort);
    }

    @Bean
    public GetWarehouseService getWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new GetWarehouseService(warehouseRepositoryPort);
    }

    @Bean
    public ActivateWarehouseService activateWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new ActivateWarehouseService(warehouseRepositoryPort);
    }

    @Bean
    public DeactivateWarehouseService deactivateWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new DeactivateWarehouseService(warehouseRepositoryPort);
    }

    @Bean
    public UpdateWarehouseLocationService updateWarehouseLocationService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new UpdateWarehouseLocationService(warehouseRepositoryPort);
    }

    @Bean
    public GetWarehousesByOwnerService getWarehousesByOwnerService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new GetWarehousesByOwnerService(warehouseRepositoryPort);
    }

    // --- Operation Audit ---

    @Bean
    public LogOperationService logOperationService(AuditLogRepositoryPort auditLogRepositoryPort) {
        return new LogOperationService(auditLogRepositoryPort);
    }

    @Bean
    public GetAllAuditLogsService getAllAuditLogsService(AuditLogRepositoryPort auditLogRepositoryPort) {
        return new GetAllAuditLogsService(auditLogRepositoryPort);
    }

    @Bean
    public GetAuditLogsByAggregateService getAuditLogsByAggregateService(AuditLogRepositoryPort auditLogRepositoryPort) {
        return new GetAuditLogsByAggregateService(auditLogRepositoryPort);
    }

    // --- Authorization ---

    @Bean
    public ValidateAuthorizationService validateAuthorizationService() {
        return new ValidateAuthorizationService();
    }
}
