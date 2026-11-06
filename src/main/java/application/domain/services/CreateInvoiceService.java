package application.domain.services;

import application.domain.enums.InvoiceStatus;
import application.domain.enums.OrderStatus;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.TaxIdentifier;

/**
 * Atomic domain service responsible exclusively for issuing the invoice of an order whose
 * payment has been validated (OBJ-09: billing of purchases). One invoice per order.
 */
public class CreateInvoiceService {

    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;

    public CreateInvoiceService(InvoiceRepositoryPort invoiceRepositoryPort, OrderRepositoryPort orderRepositoryPort) {
        if (invoiceRepositoryPort == null) {
            throw new IllegalArgumentException("InvoiceRepositoryPort cannot be null.");
        }
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    /**
     * @param orderId          paid order
     * @param buyerTaxDocument identity document of the buyer, used as the tax identifier of the invoice
     */
    public Invoice createInvoice(String orderId, String buyerTaxDocument) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        Order order = orderRepositoryPort.findOrderById(orderId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            throw new InvalidDomainStateException("Cannot issue an invoice for order '" + orderId + "' before its payment is validated.");
        }
        if (invoiceRepositoryPort.findByOrderId(order.getOrderId()).isPresent()) {
            throw new DomainException("Order '" + orderId + "' already has an invoice.");
        }
        Invoice invoice = new Invoice(buildInvoiceId(order.getOrderId()), order.getOrderId(), order.getBuyerId(),
                new TaxIdentifier(buyerTaxDocument), order.getShippingAddress(), order.getTotalAmount(), InvoiceStatus.ISSUED);
        return invoiceRepositoryPort.save(invoice);
    }

    public static String buildInvoiceId(String orderId) {
        return "INVOICE-" + orderId;
    }
}
