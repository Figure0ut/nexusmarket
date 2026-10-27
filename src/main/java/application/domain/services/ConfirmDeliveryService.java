package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;

/**
 * Atomic domain service responsible exclusively for confirming successful delivery of a shipment.
 */
public class ConfirmDeliveryService {

    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;

    public ConfirmDeliveryService(ShipmentRepositoryPort shipmentRepositoryPort,
                                  OrderRepositoryPort orderRepositoryPort) {
        if (shipmentRepositoryPort == null) {
            throw new IllegalArgumentException("ShipmentRepositoryPort cannot be null.");
        }
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public void confirmDelivery(String shipmentId) {
        if (shipmentId == null || shipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipment ID cannot be null or empty.");
        }
        Shipment shipment = shipmentRepositoryPort.findById(shipmentId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with ID: " + shipmentId));

        shipment.confirmDelivery();

        Order order = orderRepositoryPort.findOrderById(shipment.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + shipment.getOrderId()));

        order.finalizeDelivery();

        shipmentRepositoryPort.save(shipment);
        orderRepositoryPort.saveOrder(order);
    }
}
