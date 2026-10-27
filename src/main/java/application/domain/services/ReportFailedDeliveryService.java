package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Shipment;
import application.domain.ports.out.ShipmentRepositoryPort;

/**
 * Atomic domain service responsible exclusively for marking a shipment as failed delivery.
 */
public class ReportFailedDeliveryService {

    private final ShipmentRepositoryPort shipmentRepositoryPort;

    public ReportFailedDeliveryService(ShipmentRepositoryPort shipmentRepositoryPort) {
        if (shipmentRepositoryPort == null) {
            throw new IllegalArgumentException("ShipmentRepositoryPort cannot be null.");
        }
        this.shipmentRepositoryPort = shipmentRepositoryPort;
    }

    public void reportFailedDelivery(String shipmentId) {
        if (shipmentId == null || shipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipment ID cannot be null or empty.");
        }
        Shipment shipment = shipmentRepositoryPort.findById(shipmentId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with ID: " + shipmentId));

        shipment.markAsFailed();
        shipmentRepositoryPort.save(shipment);
    }
}
