package application.infrastructure.adapters.out.memory;

import application.domain.models.Invoice;
import application.domain.ports.out.InvoiceRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryInvoiceRepositoryAdapter implements InvoiceRepositoryPort {

    private final Map<String, Invoice> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<Invoice> findById(String invoiceId) {
        return Optional.ofNullable(storage.get(invoiceId));
    }

    @Override
    public Optional<Invoice> findByOrderId(String orderId) {
        return storage.values().stream()
                .filter(i -> i.getOrderId().equals(orderId))
                .findFirst();
    }

    @Override
    public Invoice save(Invoice invoice) {
        storage.put(invoice.getInvoiceId(), invoice);
        return invoice;
    }
}
