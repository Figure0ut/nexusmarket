package application.domain.services;

import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;
import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.enums.WarehouseType;
import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.NotificationPort;
import application.domain.ports.out.PaymentGatewayPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.Money;
import application.infrastructure.adapters.out.memory.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Wires the domain services with in-memory test doubles of the output ports (no Spring, no database).
 */
class MarketplaceFixture {

    final InMemoryUserRepositoryAdapter users = new InMemoryUserRepositoryAdapter();
    final InMemoryWarehouseRepositoryAdapter warehouses = new InMemoryWarehouseRepositoryAdapter();
    final InMemoryProductRepositoryAdapter products = new InMemoryProductRepositoryAdapter();
    final InMemoryInventoryRepositoryAdapter inventories = new InMemoryInventoryRepositoryAdapter();
    final InMemoryOrderRepositoryAdapter orders = new InMemoryOrderRepositoryAdapter();
    final InMemoryInvoiceRepositoryAdapter invoices = new InMemoryInvoiceRepositoryAdapter();
    final InMemoryShipmentRepositoryAdapter shipments = new InMemoryShipmentRepositoryAdapter();
    final InMemoryReturnRepositoryAdapter returns = new InMemoryReturnRepositoryAdapter();
    final InMemoryAuditLogRepositoryAdapter audit = new InMemoryAuditLogRepositoryAdapter();
    final List<String> sentEmails = new ArrayList<>();
    boolean paymentsApproved = true;

    final PaymentGatewayPort paymentGateway = new PaymentGatewayPort() {
        @Override
        public boolean processPayment(String orderId, Money amount, String paymentToken) {
            return paymentsApproved;
        }

        @Override
        public boolean processRefund(String refundId, Money amount) {
            return true;
        }
    };
    final NotificationPort notifications = (recipient, subject, body) -> sentEmails.add(recipient.getValue() + ": " + subject);

    final RegisterUserService registerUser = new RegisterUserService(users);
    final RegisterWarehouseService registerWarehouse = new RegisterWarehouseService(warehouses, users);
    final IncorporateSellerService incorporateSeller = new IncorporateSellerService(registerUser, registerWarehouse, warehouses);
    final RegisterProductService registerProduct = new RegisterProductService(products);
    final AddStockService addStock = new AddStockService(inventories, warehouses, products);
    final MarkStockDamagedService markDamaged = new MarkStockDamagedService(inventories);
    final GetOrCreateCartService getOrCreateCart = new GetOrCreateCartService(orders);
    final AddItemToCartService addItemToCart = new AddItemToCartService(orders, getOrCreateCart, products);
    final CheckoutCartService checkout = new CheckoutCartService(orders, products, inventories, warehouses);
    final CreateInvoiceService createInvoice = new CreateInvoiceService(invoices, orders);
    final ProcessOrderPaymentService pay = new ProcessOrderPaymentService(orders, paymentGateway, notifications, createInvoice);
    final PrepareShipmentService prepareShipment = new PrepareShipmentService(shipments, orders, warehouses);
    final DispatchShipmentService dispatch = new DispatchShipmentService(shipments, orders, inventories);
    final ConfirmDeliveryService confirmDelivery = new ConfirmDeliveryService(shipments, orders);
    final RequestReturnService requestReturn = new RequestReturnService(returns, orders);
    final ApproveReturnService approveReturn = new ApproveReturnService(returns);
    final RejectReturnService rejectReturn = new RejectReturnService(returns);
    final ProcessReturnAndRefundService processRefund = new ProcessReturnAndRefundService(returns, inventories, paymentGateway, orders);
    final GenerateAdministrativeReportService report = new GenerateAdministrativeReportService(users, warehouses, products,
            inventories, orders, invoices, shipments, returns);

    final User admin = registerUser.registerUser(
            new User("ADM-1", "CC-ADM", "Admin", new Email("admin@nexusmarket.com"), UserRole.ADMIN, UserStatus.ACTIVE));
    Seller seller;
    Buyer buyer;

    /** Admin incorporates a seller with its first warehouse, the seller registers a physical and a digital product. */
    MarketplaceFixture withCatalog() {
        seller = incorporateSeller.incorporateSeller(
                new Seller("SEL-1", "NIT-1", "Seller One", new Email("seller@nexusmarket.com"), UserStatus.PENDING_INCORPORATION, null, "Seller Corp"),
                new Warehouse("WH-SEL-1", "Seller Warehouse", new Address("Industrial Park 1"), WarehouseType.SELLER, "SEL-1", true),
                admin);
        registerProduct.registerProduct(new Product("P-LAPTOP", "SKU-LAPTOP", "SEL-1", "Laptop", 1000.0, ProductType.PHYSICAL, ProductStatus.PUBLISHED));
        registerProduct.registerProduct(new Product("P-EBOOK", "SKU-EBOOK", "SEL-1", "E-book", 15.0, ProductType.DIGITAL, ProductStatus.PUBLISHED));
        addStock.addStock("P-LAPTOP", "WH-SEL-1", 10);
        buyer = (Buyer) registerUser.registerUser(new Buyer("BUY-1", "CC-BUY-1", "Buyer One", "buyer@nexusmarket.com", "Main Street 1", "ENABLED"));
        return this;
    }
}
