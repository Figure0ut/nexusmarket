package application.infrastructure.adapters.out.sql;

import application.domain.models.Invoice;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.TaxIdentifier;
import application.infrastructure.adapters.out.sql.entity.InvoiceEntity;
import application.infrastructure.adapters.out.sql.repository.SpringDataJpaInvoiceRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Primary
public class JpaInvoiceRepositoryAdapter implements InvoiceRepositoryPort {

    private final SpringDataJpaInvoiceRepository invoiceRepository;

    public JpaInvoiceRepositoryAdapter(SpringDataJpaInvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Optional<Invoice> findById(String invoiceId) {
        return invoiceRepository.findById(invoiceId).map(this::toDomain);
    }

    @Override
    public Optional<Invoice> findByOrderId(String orderId) {
        return invoiceRepository.findByOrderId(orderId).map(this::toDomain);
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceEntity entity = toEntity(invoice);
        InvoiceEntity saved = invoiceRepository.save(entity);
        return toDomain(saved);
    }

    private Invoice toDomain(InvoiceEntity entity) {
        return new Invoice(
                entity.getInvoiceId(),
                entity.getOrderId(),
                entity.getBuyerId(),
                new TaxIdentifier(entity.getTaxIdentifier()),
                new Address(entity.getBillingAddress()),
                new Money(entity.getTotalAmount()),
                entity.getStatus()
        );
    }

    private InvoiceEntity toEntity(Invoice invoice) {
        InvoiceEntity entity = new InvoiceEntity();
        entity.setInvoiceId(invoice.getInvoiceId());
        entity.setOrderId(invoice.getOrderId());
        entity.setBuyerId(invoice.getBuyerId());
        entity.setTaxIdentifier(invoice.getTaxIdentifier() != null ? invoice.getTaxIdentifier().getTaxId() : "N/A");
        entity.setBillingAddress(invoice.getBillingAddress() != null ? invoice.getBillingAddress().getStreet() : "");
        entity.setTotalAmount(invoice.getTotalAmount() != null ? invoice.getTotalAmount().getAmount() : java.math.BigDecimal.ZERO);
        entity.setStatus(invoice.getStatus());
        return entity;
    }
}
