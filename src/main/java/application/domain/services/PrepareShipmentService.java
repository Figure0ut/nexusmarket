package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Shipment;
import application.domain.models.Warehouse;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;

/**
 * Atomic domain service responsible exclusively for preparing (packing) the shipment of a PAID order
 * that contains physical products (section 6.1, step 7). The shipment leaves from the warehouse
 * where the stock of the order was reserved and goes to the shipping address of the order.
 */
public class PrepareShipmentService {

    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public PrepareShipmentService(ShipmentRepositoryPort shipmentRepositoryPort,
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

    public Shipment prepareShipment(String shipmentId, String orderId) {
        if (shipmentId == null || shipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipment ID cannot be null or empty.");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        if (shipmentRepositoryPort.findById(shipmentId.trim()).isPresent()) {
            throw new DomainException("Shipment with ID '" + shipmentId + "' already exists.");
        }
        Order order = orderRepositoryPort.findOrderById(orderId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidDomainStateException("Cannot prepare shipment for order '" + orderId + "' with status: " + order.getStatus() + ". Order must be PAID.");
        }
        if (!order.containsPhysicalItems()) {
            throw new InvalidDomainStateException("Order '" + orderId + "' only contains digital products; shipments are only for physical products.");
        }
        if (shipmentRepositoryPort.findByOrderId(order.getOrderId()).isPresent()) {
            throw new DomainException("Order '" + orderId + "' already has a shipment.");
        }
        String warehouseId = order.getItems().stream()
                .filter(OrderItem::isPhysical)
                .map(OrderItem::getWarehouseId)
                .findFirst().orElseThrow();
        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + warehouseId));
        if (!warehouse.isActive()) {
            throw new InvalidDomainStateException("Cannot dispatch from inactive warehouse: " + warehouseId);
        }
        Shipment shipment = new Shipment(shipmentId.trim(), order.getOrderId(), warehouseId, order.getShippingAddress());
        return shipmentRepositoryPort.save(shipment);
    }
}
