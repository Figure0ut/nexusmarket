package application.infrastructure.config;

import application.domain.ports.out.*;
import application.domain.services.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration that wires the pure-Java domain services as beans.
 * <p>
 * The domain layer has no Spring annotations (Hexagonal Architecture): this class is the only place where
 * domain services are connected with the output adapters (SQL / MongoDB / gateways) that implement the ports.
 */
@Configuration
public class DomainServicesConfig {

    // --- Users, buyers and authorization ---

    @Bean
    public ValidateAuthorizationService validateAuthorizationService() {
        return new ValidateAuthorizationService();
    }

    @Bean
    public RegisterUserService registerUserService(UserRepositoryPort userRepositoryPort) {
        return new RegisterUserService(userRepositoryPort);
    }

    @Bean
    public AuthenticateUserService authenticateUserService(UserRepositoryPort userRepositoryPort) {
        return new AuthenticateUserService(userRepositoryPort);
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

    @Bean
    public GetAllUsersService getAllUsersService(UserRepositoryPort userRepositoryPort) {
        return new GetAllUsersService(userRepositoryPort);
    }

    @Bean
    public UpdateBuyerMainAddressService updateBuyerMainAddressService(UserRepositoryPort userRepositoryPort) {
        return new UpdateBuyerMainAddressService(userRepositoryPort);
    }

    @Bean
    public AddBuyerAdditionalAddressService addBuyerAdditionalAddressService(UserRepositoryPort userRepositoryPort) {
        return new AddBuyerAdditionalAddressService(userRepositoryPort);
    }

    // --- Sellers and warehouses ---

    @Bean
    public RegisterWarehouseService registerWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort,
                                                             UserRepositoryPort userRepositoryPort) {
        return new RegisterWarehouseService(warehouseRepositoryPort, userRepositoryPort);
    }

    @Bean
    public IncorporateSellerService incorporateSellerService(RegisterUserService registerUserService,
                                                             RegisterWarehouseService registerWarehouseService,
                                                             WarehouseRepositoryPort warehouseRepositoryPort) {
        return new IncorporateSellerService(registerUserService, registerWarehouseService, warehouseRepositoryPort);
    }

    @Bean
    public GetWarehouseService getWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new GetWarehouseService(warehouseRepositoryPort);
    }

    @Bean
    public GetAllWarehousesService getAllWarehousesService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new GetAllWarehousesService(warehouseRepositoryPort);
    }

    @Bean
    public GetWarehousesByOwnerService getWarehousesByOwnerService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new GetWarehousesByOwnerService(warehouseRepositoryPort);
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

    // --- Catalog ---

    @Bean
    public RegisterProductService registerProductService(ProductRepositoryPort productRepositoryPort) {
        return new RegisterProductService(productRepositoryPort);
    }

    @Bean
    public GetProductService getProductService(ProductRepositoryPort productRepositoryPort) {
        return new GetProductService(productRepositoryPort);
    }

    @Bean
    public GetPublishedCatalogService getPublishedCatalogService(ProductRepositoryPort productRepositoryPort) {
        return new GetPublishedCatalogService(productRepositoryPort);
    }

    @Bean
    public GetProductsBySellerService getProductsBySellerService(ProductRepositoryPort productRepositoryPort) {
        return new GetProductsBySellerService(productRepositoryPort);
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

    // --- Inventory ---

    @Bean
    public AddStockService addStockService(InventoryRepositoryPort inventoryRepositoryPort,
                                           WarehouseRepositoryPort warehouseRepositoryPort,
                                           ProductRepositoryPort productRepositoryPort) {
        return new AddStockService(inventoryRepositoryPort, warehouseRepositoryPort, productRepositoryPort);
    }

    @Bean
    public MarkStockDamagedService markStockDamagedService(InventoryRepositoryPort inventoryRepositoryPort) {
        return new MarkStockDamagedService(inventoryRepositoryPort);
    }

    @Bean
    public GetInventoryService getInventoryService(InventoryRepositoryPort inventoryRepositoryPort) {
        return new GetInventoryService(inventoryRepositoryPort);
    }

    @Bean
    public GetInventoriesByProductService getInventoriesByProductService(InventoryRepositoryPort inventoryRepositoryPort) {
        return new GetInventoriesByProductService(inventoryRepositoryPort);
    }

    @Bean
    public GetTotalAvailableStockService getTotalAvailableStockService(InventoryRepositoryPort inventoryRepositoryPort) {
        return new GetTotalAvailableStockService(inventoryRepositoryPort);
    }

    // --- Cart ---

    @Bean
    public GetOrCreateCartService getOrCreateCartService(OrderRepositoryPort orderRepositoryPort) {
        return new GetOrCreateCartService(orderRepositoryPort);
    }

    @Bean
    public AddItemToCartService addItemToCartService(OrderRepositoryPort orderRepositoryPort,
                                                     GetOrCreateCartService getOrCreateCartService,
                                                     ProductRepositoryPort productRepositoryPort) {
        return new AddItemToCartService(orderRepositoryPort, getOrCreateCartService, productRepositoryPort);
    }

    @Bean
    public RemoveItemFromCartService removeItemFromCartService(OrderRepositoryPort orderRepositoryPort) {
        return new RemoveItemFromCartService(orderRepositoryPort);
    }

    @Bean
    public ClearCartService clearCartService(OrderRepositoryPort orderRepositoryPort) {
        return new ClearCartService(orderRepositoryPort);
    }

    // --- Orders, payment and billing ---

    @Bean
    public CheckoutCartService checkoutCartService(OrderRepositoryPort orderRepositoryPort,
                                                   ProductRepositoryPort productRepositoryPort,
                                                   InventoryRepositoryPort inventoryRepositoryPort,
                                                   WarehouseRepositoryPort warehouseRepositoryPort) {
        return new CheckoutCartService(orderRepositoryPort, productRepositoryPort, inventoryRepositoryPort, warehouseRepositoryPort);
    }

    @Bean
    public CreateInvoiceService createInvoiceService(InvoiceRepositoryPort invoiceRepositoryPort, OrderRepositoryPort orderRepositoryPort) {
        return new CreateInvoiceService(invoiceRepositoryPort, orderRepositoryPort);
    }

    @Bean
    public ProcessOrderPaymentService processOrderPaymentService(OrderRepositoryPort orderRepositoryPort,
                                                                 PaymentGatewayPort paymentGatewayPort,
                                                                 NotificationPort notificationPort,
                                                                 CreateInvoiceService createInvoiceService) {
        return new ProcessOrderPaymentService(orderRepositoryPort, paymentGatewayPort, notificationPort, createInvoiceService);
    }

    @Bean
    public GetOrderService getOrderService(OrderRepositoryPort orderRepositoryPort) {
        return new GetOrderService(orderRepositoryPort);
    }

    @Bean
    public GetOrdersByBuyerService getOrdersByBuyerService(OrderRepositoryPort orderRepositoryPort) {
        return new GetOrdersByBuyerService(orderRepositoryPort);
    }

    @Bean
    public GetAllOrdersService getAllOrdersService(OrderRepositoryPort orderRepositoryPort) {
        return new GetAllOrdersService(orderRepositoryPort);
    }

    @Bean
    public GetInvoiceService getInvoiceService(InvoiceRepositoryPort invoiceRepositoryPort) {
        return new GetInvoiceService(invoiceRepositoryPort);
    }

    @Bean
    public GetInvoiceByOrderService getInvoiceByOrderService(InvoiceRepositoryPort invoiceRepositoryPort) {
        return new GetInvoiceByOrderService(invoiceRepositoryPort);
    }

    // --- Logistics ---

    @Bean
    public PrepareShipmentService prepareShipmentService(ShipmentRepositoryPort shipmentRepositoryPort,
                                                         OrderRepositoryPort orderRepositoryPort,
                                                         WarehouseRepositoryPort warehouseRepositoryPort) {
        return new PrepareShipmentService(shipmentRepositoryPort, orderRepositoryPort, warehouseRepositoryPort);
    }

    @Bean
    public DispatchShipmentService dispatchShipmentService(ShipmentRepositoryPort shipmentRepositoryPort,
                                                           OrderRepositoryPort orderRepositoryPort,
                                                           InventoryRepositoryPort inventoryRepositoryPort) {
        return new DispatchShipmentService(shipmentRepositoryPort, orderRepositoryPort, inventoryRepositoryPort);
    }

    @Bean
    public ConfirmDeliveryService confirmDeliveryService(ShipmentRepositoryPort shipmentRepositoryPort,
                                                         OrderRepositoryPort orderRepositoryPort) {
        return new ConfirmDeliveryService(shipmentRepositoryPort, orderRepositoryPort);
    }

    @Bean
    public GetShipmentService getShipmentService(ShipmentRepositoryPort shipmentRepositoryPort) {
        return new GetShipmentService(shipmentRepositoryPort);
    }

    // --- Returns and refunds ---

    @Bean
    public RequestReturnService requestReturnService(ReturnRepositoryPort returnRepositoryPort, OrderRepositoryPort orderRepositoryPort) {
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
                                                                       PaymentGatewayPort paymentGatewayPort,
                                                                       OrderRepositoryPort orderRepositoryPort) {
        return new ProcessReturnAndRefundService(returnRepositoryPort, inventoryRepositoryPort, paymentGatewayPort, orderRepositoryPort);
    }

    // --- Audit and administrative reports ---

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

    @Bean
    public GenerateAdministrativeReportService generateAdministrativeReportService(UserRepositoryPort userRepositoryPort,
                                                                                   WarehouseRepositoryPort warehouseRepositoryPort,
                                                                                   ProductRepositoryPort productRepositoryPort,
                                                                                   InventoryRepositoryPort inventoryRepositoryPort,
                                                                                   OrderRepositoryPort orderRepositoryPort,
                                                                                   InvoiceRepositoryPort invoiceRepositoryPort,
                                                                                   ShipmentRepositoryPort shipmentRepositoryPort,
                                                                                   ReturnRepositoryPort returnRepositoryPort) {
        return new GenerateAdministrativeReportService(userRepositoryPort, warehouseRepositoryPort, productRepositoryPort,
                inventoryRepositoryPort, orderRepositoryPort, invoiceRepositoryPort, shipmentRepositoryPort, returnRepositoryPort);
    }
}
