package application.infrastructure.adapters.out.sql.repository;

import application.infrastructure.adapters.out.sql.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataJpaInvoiceRepository extends JpaRepository<InvoiceEntity, String> {
    Optional<InvoiceEntity> findByOrderId(String orderId);
}
