package application.infrastructure.adapters.out.sql.repository;

import application.infrastructure.adapters.out.sql.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataJpaInventoryRepository extends JpaRepository<InventoryEntity, String> {
    Optional<InventoryEntity> findByProductIdAndWarehouseId(String productId, String warehouseId);
    List<InventoryEntity> findByProductId(String productId);
}
