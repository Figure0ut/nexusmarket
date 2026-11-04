package application.infrastructure.adapters.out.sql.repository;

import application.infrastructure.adapters.out.sql.entity.ReturnRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaReturnRepository extends JpaRepository<ReturnRequestEntity, String> {
}
