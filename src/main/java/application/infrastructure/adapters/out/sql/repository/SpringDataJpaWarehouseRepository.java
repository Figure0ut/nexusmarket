package application.infrastructure.adapters.out.sql.repository;

import application.infrastructure.adapters.out.sql.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataJpaWarehouseRepository extends JpaRepository<WarehouseEntity, String> {
    List<WarehouseEntity> findByOwnerId(String ownerId);
}
