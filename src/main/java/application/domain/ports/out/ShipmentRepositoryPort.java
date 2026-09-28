package application.domain.ports.out;

import application.domain.models.Shipment;

import java.util.Optional;

public interface ShipmentRepositoryPort {
    Optional<Shipment> findById(String shipmentId);
    Optional<Shipment> findByOrderId(String orderId);
    Shipment save(Shipment shipment);
}
