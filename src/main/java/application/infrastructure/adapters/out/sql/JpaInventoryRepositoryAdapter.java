package application.infrastructure.adapters.out.sql;

import application.domain.models.Inventory;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.StockQuantity;
import application.infrastructure.adapters.out.sql.entity.InventoryEntity;
import application.infrastructure.adapters.out.sql.repository.SpringDataJpaInventoryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Primary
public class JpaInventoryRepositoryAdapter implements InventoryRepositoryPort {

    private final SpringDataJpaInventoryRepository jpaRepository;

    public JpaInventoryRepositoryAdapter(SpringDataJpaInventoryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId) {
        if (productId == null || warehouseId == null) return Optional.empty();
        return jpaRepository.findByProductIdAndWarehouseId(productId, warehouseId).map(this::toDomain);
    }

    @Override
    public List<Inventory> findByProductId(String productId) {
        if (productId == null) return List.of();
        return jpaRepository.findByProductId(productId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Inventory> findByWarehouseId(String warehouseId) {
        if (warehouseId == null) return List.of();
        return jpaRepository.findByWarehouseId(warehouseId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Inventory> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Inventory save(Inventory inventory) {
        InventoryEntity entity = toEntity(inventory);
        InventoryEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private Inventory toDomain(InventoryEntity entity) {
        if (entity == null) return null;
        Inventory inventory = new Inventory(
                entity.getInventoryId(),
                entity.getProductId(),
                entity.getWarehouseId(),
                new StockQuantity(entity.getAvailableStock()),
                new StockQuantity(entity.getReservedStock()),
                new StockQuantity(entity.getDamagedStock())
        );
        inventory.setLastMovementType(entity.getLastMovementType());
        return inventory;
    }

    private InventoryEntity toEntity(Inventory inventory) {
        if (inventory == null) return null;
        InventoryEntity entity = new InventoryEntity();
        entity.setInventoryId(inventory.getInventoryId());
        entity.setProductId(inventory.getProductId());
        entity.setWarehouseId(inventory.getWarehouseId());
        entity.setAvailableStock(inventory.getAvailableStock() != null ? inventory.getAvailableStock().getValue() : 0);
        entity.setReservedStock(inventory.getReservedStock() != null ? inventory.getReservedStock().getValue() : 0);
        entity.setDamagedStock(inventory.getDamagedStock() != null ? inventory.getDamagedStock().getValue() : 0);
        entity.setLastMovementType(inventory.getLastMovementType());
        return entity;
    }
}
