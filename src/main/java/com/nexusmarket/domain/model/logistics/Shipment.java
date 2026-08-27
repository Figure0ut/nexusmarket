package com.nexusmarket.domain.model.logistics;

import com.nexusmarket.domain.model.common.Address;

import java.util.Objects;

/**
 * Pure Java Domain Aggregate Root representing a physical logistics Shipment.
 */
public class Shipment {

    private final String shipmentId;
    private final String orderId;
    private final String warehouseId;
    private final Address destinationAddress;
    private String carrier;
    private String trackingNumber;
    private ShipmentStatus status;

    public Shipment(String shipmentId, String orderId, String warehouseId, Address destinationAddress,
                    String carrier, String trackingNumber, ShipmentStatus status) {
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
        if (status == null) {
            throw new IllegalArgumentException("Shipment status cannot be null.");
        }

        this.shipmentId = shipmentId.trim();
        this.orderId = orderId.trim();
        this.warehouseId = warehouseId.trim();
        this.destinationAddress = destinationAddress;
        this.carrier = carrier != null ? carrier.trim() : "";
        this.trackingNumber = trackingNumber != null ? trackingNumber.trim() : "";
        this.status = status;
    }

    public Shipment(String shipmentId, String orderId, String warehouseId, Address destinationAddress) {
        this(shipmentId, orderId, warehouseId, destinationAddress, "", "", ShipmentStatus.PREPARING);
    }

    public void dispatch(String carrier, String trackingNumber) {
        if (carrier == null || carrier.trim().isEmpty()) {
            throw new IllegalArgumentException("Carrier name cannot be null or empty.");
        }
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Tracking number cannot be null or empty.");
        }
        this.carrier = carrier.trim();
        this.trackingNumber = trackingNumber.trim();
        this.status = ShipmentStatus.IN_TRANSIT;
    }

    public void confirmDelivery() {
        if (status != ShipmentStatus.IN_TRANSIT) {
            throw new IllegalStateException("Shipment must be IN_TRANSIT to confirm delivery.");
        }
        this.status = ShipmentStatus.DELIVERED;
    }

    public void markAsFailed() {
        this.status = ShipmentStatus.FAILED;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public Address getDestinationAddress() {
        return destinationAddress;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipment shipment = (Shipment) o;
        return Objects.equals(shipmentId, shipment.shipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipmentId);
    }
}
