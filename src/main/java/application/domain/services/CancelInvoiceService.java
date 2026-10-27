package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Invoice;
import application.domain.ports.out.InvoiceRepositoryPort;

/**
 * Atomic domain service responsible exclusively for cancelling an invoice.
 */
public class CancelInvoiceService {

    private final InvoiceRepositoryPort invoiceRepositoryPort;

    public CancelInvoiceService(InvoiceRepositoryPort invoiceRepositoryPort) {
        if (invoiceRepositoryPort == null) {
            throw new IllegalArgumentException("InvoiceRepositoryPort cannot be null.");
        }
        this.invoiceRepositoryPort = invoiceRepositoryPort;
    }

    public void cancelInvoice(String invoiceId) {
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invoice ID cannot be null or empty.");
        }
        Invoice invoice = invoiceRepositoryPort.findById(invoiceId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + invoiceId));
        invoice.cancel();
        invoiceRepositoryPort.save(invoice);
    }
}
