package application.infrastructure.adapters.out.sql.repository;

import application.infrastructure.adapters.out.sql.entity.RefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaRefundRepository extends JpaRepository<RefundEntity, String> {
}
