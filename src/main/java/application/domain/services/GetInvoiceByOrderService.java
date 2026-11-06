package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Invoice;
import application.domain.ports.out.InvoiceRepositoryPort;

/**
 * Atomic domain service responsible exclusively for querying the invoice of an order.
 */
public class GetInvoiceByOrderService {

    private final InvoiceRepositoryPort repositoryPort;

    public GetInvoiceByOrderService(InvoiceRepositoryPort repositoryPort) {
        if (repositoryPort == null) {
            throw new IllegalArgumentException("InvoiceRepositoryPort cannot be null.");
        }
        this.repositoryPort = repositoryPort;
    }

    public Invoice getInvoiceByOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        return repositoryPort.findByOrderId(orderId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found for order: " + orderId));
    }
}
