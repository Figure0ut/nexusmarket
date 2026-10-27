package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Shipment;
import application.domain.ports.out.ShipmentRepositoryPort;

/**
 * Atomic domain service responsible exclusively for querying a shipment by its identifier.
 */
public class GetShipmentService {

    private final ShipmentRepositoryPort shipmentRepositoryPort;

    public GetShipmentService(ShipmentRepositoryPort shipmentRepositoryPort) {
        if (shipmentRepositoryPort == null) {
            throw new IllegalArgumentException("ShipmentRepositoryPort cannot be null.");
        }
        this.shipmentRepositoryPort = shipmentRepositoryPort;
    }

    public Shipment getShipment(String shipmentId) {
        if (shipmentId == null || shipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipment ID cannot be null or empty.");
        }
        return shipmentRepositoryPort.findById(shipmentId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with ID: " + shipmentId));
    }
}
