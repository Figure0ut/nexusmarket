package application.infrastructure.adapters.out.memory;

import application.domain.models.Shipment;
import application.domain.ports.out.ShipmentRepositoryPort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory test double of the output port (used only by unit tests).
 */
public class InMemoryShipmentRepositoryAdapter implements ShipmentRepositoryPort {

    private final Map<String, Shipment> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<Shipment> findById(String shipmentId) {
        return Optional.ofNullable(storage.get(shipmentId));
    }

    @Override
    public Optional<Shipment> findByOrderId(String orderId) {
        return storage.values().stream()
                .filter(s -> s.getOrderId().equals(orderId))
                .findFirst();
    }

    @Override
    public Shipment save(Shipment shipment) {
        storage.put(shipment.getShipmentId(), shipment);
        return shipment;
    }

    @Override
    public List<Shipment> findAll() {
        return List.copyOf(storage.values());
    }
}
