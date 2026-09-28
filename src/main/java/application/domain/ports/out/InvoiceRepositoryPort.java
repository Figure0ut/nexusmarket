package application.domain.ports.out;

import application.domain.models.Invoice;

import java.util.Optional;

public interface InvoiceRepositoryPort {
    Optional<Invoice> findById(String invoiceId);
    Optional<Invoice> findByOrderId(String orderId);
    Invoice save(Invoice invoice);
}
