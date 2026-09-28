package application.domain.services;

import application.domain.enums.InvoiceStatus;
import application.domain.enums.OrderStatus;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.TaxIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BillingInvoicingServiceTest {

    private BillingInvoicingService billingService;
    private InMemoryInvoiceRepository invoiceRepository;
    private MockOrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        invoiceRepository = new InMemoryInvoiceRepository();
        orderRepository = new MockOrderRepository();
        billingService = new BillingInvoicingService(invoiceRepository, orderRepository);
    }

    @Test
    @DisplayName("Should generate invoice for paid order and manage payment/cancellation")
    void shouldGenerateAndManageInvoice() {
        Order order = new Order("ORD-1", "BUY-1", List.of(new OrderItem("P-1", "Prod", new Money(100.00), 1)), new Address("123 St", "City", "ST", "111", "USA"));
        order.markAsPaid();
        orderRepository.save(order);

        Invoice invoice = billingService.generateInvoice(
                "INV-1", "ORD-1", "BUY-1", new TaxIdentifier("TAX-12345"), new Address("123 St", "City", "ST", "111", "USA")
        );

        assertNotNull(invoice);
        assertEquals(InvoiceStatus.ISSUED, invoice.getStatus());
        assertEquals(new Money(100.00), invoice.getTotalAmount());

        billingService.markInvoicePaid("INV-1");
        assertEquals(InvoiceStatus.PAID, billingService.getInvoice("INV-1").getStatus());

        billingService.cancelInvoice("INV-1");
        assertEquals(InvoiceStatus.CANCELLED, billingService.getInvoice("INV-1").getStatus());
    }

    @Test
    @DisplayName("Should reject generating invoice for unpaid order")
    void shouldRejectInvoiceForUnpaidOrder() {
        Order order = new Order("ORD-1", "BUY-1", List.of(new OrderItem("P-1", "Prod", new Money(100.00), 1)), new Address("123 St", "City", "ST", "111", "USA"));
        orderRepository.save(order);

        assertThrows(InvalidDomainStateException.class, () ->
                billingService.generateInvoice("INV-1", "ORD-1", "BUY-1", new TaxIdentifier("TAX-12345"), new Address("123 St", "City", "ST", "111", "USA"))
        );
    }

    private static class InMemoryInvoiceRepository implements InvoiceRepositoryPort {
        private final Map<String, Invoice> storage = new HashMap<>();

        @Override
        public Optional<Invoice> findById(String invoiceId) {
            return Optional.ofNullable(storage.get(invoiceId));
        }

        @Override
        public Optional<Invoice> findByOrderId(String orderId) {
            return storage.values().stream().filter(i -> i.getOrderId().equals(orderId)).findFirst();
        }

        @Override
        public Invoice save(Invoice invoice) {
            storage.put(invoice.getInvoiceId(), invoice);
            return invoice;
        }
    }

    private static class MockOrderRepository implements OrderRepositoryPort {
        private final Map<String, Order> orders = new HashMap<>();

        void save(Order order) {
            orders.put(order.getOrderId(), order);
        }

        @Override
        public Optional<Order> findOrderById(String orderId) {
            return Optional.ofNullable(orders.get(orderId));
        }

        @Override public Optional<application.domain.models.Cart> findCartByBuyerId(String buyerId) { return Optional.empty(); }
        @Override public application.domain.models.Cart saveCart(application.domain.models.Cart cart) { return cart; }
        @Override public List<Order> findOrdersByBuyerId(String buyerId) { return List.of(); }
        @Override public Order saveOrder(Order order) { orders.put(order.getOrderId(), order); return order; }
    }
}
