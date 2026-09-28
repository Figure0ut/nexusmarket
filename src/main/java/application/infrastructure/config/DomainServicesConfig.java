package application.infrastructure.config;

import application.domain.ports.out.*;
import application.domain.services.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServicesConfig {

    @Bean
    public UserAuthenticationService userAuthenticationService(UserRepositoryPort userRepositoryPort) {
        return new UserAuthenticationService(userRepositoryPort);
    }

    @Bean
    public SellerIncorporationService sellerIncorporationService(UserRepositoryPort userRepositoryPort) {
        return new SellerIncorporationService(userRepositoryPort);
    }

    @Bean
    public CatalogManagementService catalogManagementService(ProductRepositoryPort productRepositoryPort) {
        return new CatalogManagementService(productRepositoryPort);
    }

    @Bean
    public WarehouseManagementService warehouseManagementService(WarehouseRepositoryPort warehouseRepositoryPort) {
        return new WarehouseManagementService(warehouseRepositoryPort);
    }

    @Bean
    public InventoryAllocationService inventoryAllocationService(InventoryRepositoryPort inventoryRepositoryPort,
                                                                 WarehouseRepositoryPort warehouseRepositoryPort) {
        return new InventoryAllocationService(inventoryRepositoryPort, warehouseRepositoryPort);
    }

    @Bean
    public OrderCheckoutService orderCheckoutService(OrderRepositoryPort orderRepositoryPort,
                                                     PaymentGatewayPort paymentGatewayPort,
                                                     NotificationPort notificationPort) {
        return new OrderCheckoutService(orderRepositoryPort, paymentGatewayPort, notificationPort);
    }

    @Bean
    public BillingInvoicingService billingInvoicingService(InvoiceRepositoryPort invoiceRepositoryPort,
                                                           OrderRepositoryPort orderRepositoryPort) {
        return new BillingInvoicingService(invoiceRepositoryPort, orderRepositoryPort);
    }

    @Bean
    public LogisticsDispatchService logisticsDispatchService(ShipmentRepositoryPort shipmentRepositoryPort,
                                                             OrderRepositoryPort orderRepositoryPort,
                                                             WarehouseRepositoryPort warehouseRepositoryPort) {
        return new LogisticsDispatchService(shipmentRepositoryPort, orderRepositoryPort, warehouseRepositoryPort);
    }

    @Bean
    public ReturnRefundService returnRefundService(ReturnRepositoryPort returnRepositoryPort,
                                                   OrderRepositoryPort orderRepositoryPort,
                                                   InventoryRepositoryPort inventoryRepositoryPort,
                                                   PaymentGatewayPort paymentGatewayPort) {
        return new ReturnRefundService(returnRepositoryPort, orderRepositoryPort, inventoryRepositoryPort, paymentGatewayPort);
    }

    @Bean
    public OperationAuditService operationAuditService(AuditLogRepositoryPort auditLogRepositoryPort) {
        return new OperationAuditService(auditLogRepositoryPort);
    }

    @Bean
    public AuthorizationService authorizationService() {
        return new AuthorizationService();
    }
}
