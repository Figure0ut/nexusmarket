package application.infrastructure.adapters.out.sql;

import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Address;
import application.infrastructure.adapters.out.sql.entity.WarehouseEntity;
import application.infrastructure.adapters.out.sql.repository.SpringDataJpaWarehouseRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Primary
public class JpaWarehouseRepositoryAdapter implements WarehouseRepositoryPort {

    private final SpringDataJpaWarehouseRepository jpaRepository;

    public JpaWarehouseRepositoryAdapter(SpringDataJpaWarehouseRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Warehouse> findById(String warehouseId) {
        if (warehouseId == null) return Optional.empty();
        return jpaRepository.findById(warehouseId).map(this::toDomain);
    }

    @Override
    public List<Warehouse> findByOwnerId(String ownerId) {
        if (ownerId == null) return List.of();
        return jpaRepository.findByOwnerId(ownerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Warehouse> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        WarehouseEntity entity = toEntity(warehouse);
        WarehouseEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private Warehouse toDomain(WarehouseEntity entity) {
        if (entity == null) return null;
        return new Warehouse(
                entity.getWarehouseId(),
                entity.getName(),
                new Address(entity.getLocation()),
                entity.getType(),
                entity.getOwnerId(),
                entity.isActive()
        );
    }

    private WarehouseEntity toEntity(Warehouse warehouse) {
        if (warehouse == null) return null;
        WarehouseEntity entity = new WarehouseEntity();
        entity.setWarehouseId(warehouse.getWarehouseId());
        entity.setName(warehouse.getName());
        entity.setLocation(warehouse.getLocation() != null ? warehouse.getLocation().getStreet() : null);
        entity.setType(warehouse.getType());
        entity.setOwnerId(warehouse.getOwnerId());
        entity.setActive(warehouse.isActive());
        return entity;
    }
}
