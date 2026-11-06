package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Shipment;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;

/**
 * Atomic domain service responsible exclusively for dispatching a prepared shipment
 * (order PAID -> DISPATCHED: "physical exit from the warehouse").
 * The reserved stock of every physical item is consumed (OUTFLOW_SALE movement).
 */
public class DispatchShipmentService {

    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;

    public DispatchShipmentService(ShipmentRepositoryPort shipmentRepositoryPort,
                                   OrderRepositoryPort orderRepositoryPort,
                                   InventoryRepositoryPort inventoryRepositoryPort) {
        if (shipmentRepositoryPort == null) {
            throw new IllegalArgumentException("ShipmentRepositoryPort cannot be null.");
        }
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public Shipment dispatchShipment(String shipmentId, String carrier, String trackingNumber) {
        if (shipmentId == null || shipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipment ID cannot be null or empty.");
        }
        Shipment shipment = shipmentRepositoryPort.findById(shipmentId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with ID: " + shipmentId));
        Order order = orderRepositoryPort.findOrderById(shipment.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + shipment.getOrderId()));

        // Validate both state transitions before persisting anything.
        shipment.dispatch(carrier, trackingNumber);
        order.markAsDispatched();

        for (OrderItem item : order.getItems()) {
            if (!item.isPhysical()) {
                continue;
            }
            Inventory inventory = inventoryRepositoryPort.findByProductAndWarehouse(item.getProductId(), item.getWarehouseId())
                    .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product '" + item.getProductId()
                            + "' in warehouse '" + item.getWarehouseId() + "'."));
            inventory.confirmSale(item.getQuantity());
            inventoryRepositoryPort.save(inventory);
        }
        orderRepositoryPort.saveOrder(order);
        return shipmentRepositoryPort.save(shipment);
    }
}
