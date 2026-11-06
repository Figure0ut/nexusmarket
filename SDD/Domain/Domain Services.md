# Domain Services Specification

Each business operation is an **atomic, single-responsibility service** in `application.domain.services` (56 services). They are 100% pure Java: they receive output ports through the constructor and are wired as Spring beans only in `infrastructure/config/DomainServicesConfig`.

```
[ REST controller (inbound adapter) ]
        │  resolves the authenticated user, checks authorization
        ▼
[ Atomic domain service ] ──uses──> models, value objects, enums
        │
        ▼
[ Output port ] ──implemented by──> [ SQL / MongoDB / gateway adapter ]
```

## Users & buyers ([spec](./services/user-authentication-services.md))
- `RegisterUserService`
- `AuthenticateUserService`
- `BlockUserService`
- `ActivateUserService`
- `GetUserService`
- `GetAllUsersService`
- `UpdateBuyerMainAddressService`
- `AddBuyerAdditionalAddressService`

## Authorization ([spec](./services/authorization-services.md))
- `ValidateAuthorizationService`

## Sellers ([spec](./services/seller-incorporation-services.md))
- `IncorporateSellerService`
- `GetProductsBySellerService`

## Warehouses ([spec](./services/warehouse-management-services.md))
- `RegisterWarehouseService`
- `GetWarehouseService`
- `GetAllWarehousesService`
- `GetWarehousesByOwnerService`
- `ActivateWarehouseService`
- `DeactivateWarehouseService`
- `UpdateWarehouseLocationService`

## Catalog ([spec](./services/catalog-management-services.md))
- `RegisterProductService`
- `GetProductService`
- `GetPublishedCatalogService`
- `PublishProductService`
- `SuspendProductService`
- `DiscontinueProductService`
- `UpdateProductPriceService`
- `AddProductVariantService`

## Inventory ([spec](./services/inventory-allocation-services.md))
- `AddStockService`
- `MarkStockDamagedService`
- `GetInventoryService`
- `GetInventoriesByProductService`
- `GetTotalAvailableStockService`

## Cart ([spec](./services/cart-management-services.md))
- `GetOrCreateCartService`
- `AddItemToCartService`
- `RemoveItemFromCartService`
- `ClearCartService`

## Orders ([spec](./services/order-checkout-services.md))
- `CheckoutCartService`
- `ProcessOrderPaymentService`
- `GetOrderService`
- `GetOrdersByBuyerService`
- `GetAllOrdersService`

## Billing ([spec](./services/billing-invoicing-services.md))
- `CreateInvoiceService`
- `GetInvoiceService`
- `GetInvoiceByOrderService`

## Logistics ([spec](./services/logistics-dispatch-services.md))
- `PrepareShipmentService`
- `DispatchShipmentService`
- `ConfirmDeliveryService`
- `GetShipmentService`

## Returns & refunds ([spec](./services/return-refund-services.md))
- `RequestReturnService`
- `ApproveReturnService`
- `RejectReturnService`
- `ProcessReturnAndRefundService`
- `GetReturnRequestService`

## Audit & reports ([spec](./services/operation-audit-services.md))
- `LogOperationService`
- `GetAllAuditLogsService`
- `GetAuditLogsByAggregateService`
- `GenerateAdministrativeReportService`
