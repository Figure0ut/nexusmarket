package application.domain.services;

import application.domain.enums.InvoiceStatus;
import application.domain.enums.OrderStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.TaxIdentifier;

public class BillingInvoicingService {

    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;

    public BillingInvoicingService(InvoiceRepositoryPort invoiceRepositoryPort, OrderRepositoryPort orderRepositoryPort) {
        if (invoiceRepositoryPort == null) {
            throw new IllegalArgumentException("InvoiceRepositoryPort cannot be null.");
        }
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public Invoice generateInvoice(String invoiceId, String orderId, String buyerId, TaxIdentifier taxIdentifier, Address billingAddress) {
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invoice ID cannot be null or empty.");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        if (taxIdentifier == null) {
            throw new IllegalArgumentException("Tax identifier cannot be null.");
        }
        if (billingAddress == null) {
            throw new IllegalArgumentException("Billing address cannot be null.");
        }

        Order order = orderRepositoryPort.findOrderById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.PENDING_PAYMENT || order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidDomainStateException("Cannot generate invoice for order '" + orderId + "' with status: " + order.getStatus());
        }

        Invoice invoice = new Invoice(invoiceId, orderId, buyerId, taxIdentifier, billingAddress, order.getTotalAmount(), InvoiceStatus.ISSUED);
        return invoiceRepositoryPort.save(invoice);
    }

    public Invoice getInvoice(String invoiceId) {
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invoice ID cannot be null or empty.");
        }
        return invoiceRepositoryPort.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + invoiceId));
    }

    public void markInvoicePaid(String invoiceId) {
        Invoice invoice = getInvoice(invoiceId);
        invoice.markAsPaid();
        invoiceRepositoryPort.save(invoice);
    }

    public void cancelInvoice(String invoiceId) {
        Invoice invoice = getInvoice(invoiceId);
        invoice.cancel();
        invoiceRepositoryPort.save(invoice);
    }
}
