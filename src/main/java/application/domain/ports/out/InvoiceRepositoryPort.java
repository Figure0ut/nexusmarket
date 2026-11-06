package application.domain.ports.out;

import application.domain.models.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepositoryPort {
    Optional<Invoice> findById(String invoiceId);
    Optional<Invoice> findByOrderId(String orderId);
    List<Invoice> findAll();
    Invoice save(Invoice invoice);
}
