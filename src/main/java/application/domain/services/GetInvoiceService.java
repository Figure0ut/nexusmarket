package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Invoice;
import application.domain.ports.out.InvoiceRepositoryPort;

/**
 * Atomic domain service responsible exclusively for querying an invoice by its identifier.
 */
public class GetInvoiceService {

    private final InvoiceRepositoryPort invoiceRepositoryPort;

    public GetInvoiceService(InvoiceRepositoryPort invoiceRepositoryPort) {
        if (invoiceRepositoryPort == null) {
            throw new IllegalArgumentException("InvoiceRepositoryPort cannot be null.");
        }
        this.invoiceRepositoryPort = invoiceRepositoryPort;
    }

    public Invoice getInvoice(String invoiceId) {
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invoice ID cannot be null or empty.");
        }
        return invoiceRepositoryPort.findById(invoiceId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + invoiceId));
    }
}
