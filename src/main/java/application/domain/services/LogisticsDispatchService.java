package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.Warehouse;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Address;

public class LogisticsDispatchService {

    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public LogisticsDispatchService(ShipmentRepositoryPort shipmentRepositoryPort,
                                    OrderRepositoryPort orderRepositoryPort,
                                    WarehouseRepositoryPort warehouseRepositoryPort) {
        if (shipmentRepositoryPort == null) {
            throw new IllegalArgumentException("ShipmentRepositoryPort cannot be null.");
        }
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public Shipment prepareShipment(String shipmentId, String orderId, String warehouseId, Address destinationAddress) {
        if (shipmentId == null || shipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipment ID cannot be null or empty.");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        if (destinationAddress == null) {
            throw new IllegalArgumentException("Destination address cannot be null.");
        }

        Order order = orderRepositoryPort.findOrderById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidDomainStateException("Cannot prepare shipment for order '" + orderId + "' with status: " + order.getStatus() + ". Order must be PAID.");
        }

        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + warehouseId));

        if (!warehouse.isActive()) {
            throw new InvalidDomainStateException("Cannot dispatch from inactive warehouse: " + warehouseId);
        }

        Shipment shipment = new Shipment(shipmentId, orderId, warehouseId, destinationAddress);
        return shipmentRepositoryPort.save(shipment);
    }

    public Shipment getShipment(String shipmentId) {
        if (shipmentId == null || shipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipment ID cannot be null or empty.");
        }
        return shipmentRepositoryPort.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found with ID: " + shipmentId));
    }

    public void dispatchShipment(String shipmentId, String carrier, String trackingNumber) {
        Shipment shipment = getShipment(shipmentId);
        shipment.dispatch(carrier, trackingNumber);

        Order order = orderRepositoryPort.findOrderById(shipment.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + shipment.getOrderId()));

        order.markAsDispatched();

        shipmentRepositoryPort.save(shipment);
        orderRepositoryPort.saveOrder(order);
    }

    public void confirmDelivery(String shipmentId) {
        Shipment shipment = getShipment(shipmentId);
        shipment.confirmDelivery();

        Order order = orderRepositoryPort.findOrderById(shipment.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + shipment.getOrderId()));

        order.finalizeDelivery();

        shipmentRepositoryPort.save(shipment);
        orderRepositoryPort.saveOrder(order);
    }

    public void reportFailedDelivery(String shipmentId) {
        Shipment shipment = getShipment(shipmentId);
        shipment.markAsFailed();
        shipmentRepositoryPort.save(shipment);
    }
}
