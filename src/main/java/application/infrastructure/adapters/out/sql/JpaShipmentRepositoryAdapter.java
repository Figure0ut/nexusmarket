package application.infrastructure.adapters.out.sql;

import application.domain.models.Shipment;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.valueobjects.Address;
import application.infrastructure.adapters.out.sql.entity.ShipmentEntity;
import application.infrastructure.adapters.out.sql.repository.SpringDataJpaShipmentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Primary
public class JpaShipmentRepositoryAdapter implements ShipmentRepositoryPort {

    private final SpringDataJpaShipmentRepository shipmentRepository;

    public JpaShipmentRepositoryAdapter(SpringDataJpaShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Override
    public Optional<Shipment> findById(String shipmentId) {
        return shipmentRepository.findById(shipmentId).map(this::toDomain);
    }

    @Override
    public Optional<Shipment> findByOrderId(String orderId) {
        return shipmentRepository.findByOrderId(orderId).map(this::toDomain);
    }

    @Override
    public List<Shipment> findAll() {
        return shipmentRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Shipment save(Shipment shipment) {
        ShipmentEntity entity = toEntity(shipment);
        ShipmentEntity saved = shipmentRepository.save(entity);
        return toDomain(saved);
    }

    private Shipment toDomain(ShipmentEntity entity) {
        return new Shipment(
                entity.getShipmentId(),
                entity.getOrderId(),
                entity.getWarehouseId(),
                new Address(entity.getDestinationAddress()),
                entity.getCarrier(),
                entity.getTrackingNumber(),
                entity.getStatus()
        );
    }

    private ShipmentEntity toEntity(Shipment shipment) {
        ShipmentEntity entity = new ShipmentEntity();
        entity.setShipmentId(shipment.getShipmentId());
        entity.setOrderId(shipment.getOrderId());
        entity.setWarehouseId(shipment.getWarehouseId());
        entity.setDestinationAddress(shipment.getDestinationAddress() != null ? shipment.getDestinationAddress().getStreet() : "");
        entity.setCarrier(shipment.getCarrier());
        entity.setTrackingNumber(shipment.getTrackingNumber());
        entity.setStatus(shipment.getStatus());
        return entity;
    }
}
