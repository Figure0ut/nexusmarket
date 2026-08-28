package application.domain.models;

import application.domain.enums.ShipmentStatus;
import application.domain.valueobjects.Address;

import java.util.Objects;

public class Shipment {

    private String shipmentId;
    private String orderId;
    private String warehouseId;
    private Address destinationAddress;
    private String carrier;
    private String trackingNumber;
    private ShipmentStatus status;

    public Shipment() {
        this.status = ShipmentStatus.PREPARING;
    }

    public Shipment(String shipmentId, String orderId, String warehouseId, Address destinationAddress,
                    String carrier, String trackingNumber, ShipmentStatus status) {
        setShipmentId(shipmentId);
        setOrderId(orderId);
        setWarehouseId(warehouseId);
        setDestinationAddress(destinationAddress);
        setCarrier(carrier);
        setTrackingNumber(trackingNumber);
        setStatus(status);
    }

    public Shipment(String shipmentId, String orderId, String warehouseId, Address destinationAddress) {
        this(shipmentId, orderId, warehouseId, destinationAddress, "", "", ShipmentStatus.PREPARING);
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        if (shipmentId == null || shipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipment ID cannot be null or empty.");
        }
        this.shipmentId = shipmentId.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        this.orderId = orderId.trim();
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        this.warehouseId = warehouseId.trim();
    }

    public Address getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(Address destinationAddress) {
        if (destinationAddress == null) {
            throw new IllegalArgumentException("Destination address cannot be null.");
        }
        this.destinationAddress = destinationAddress;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier != null ? carrier.trim() : "";
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber != null ? trackingNumber.trim() : "";
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Shipment status cannot be null.");
        }
        this.status = status;
    }

    public void dispatch(String carrier, String trackingNumber) {
        if (carrier == null || carrier.trim().isEmpty()) {
            throw new IllegalArgumentException("Carrier name cannot be null or empty.");
        }
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Tracking number cannot be null or empty.");
        }
        setCarrier(carrier);
        setTrackingNumber(trackingNumber);
        setStatus(ShipmentStatus.IN_TRANSIT);
    }

    public void confirmDelivery() {
        if (status != ShipmentStatus.IN_TRANSIT) {
            throw new IllegalStateException("Shipment must be IN_TRANSIT to confirm delivery.");
        }
        setStatus(ShipmentStatus.DELIVERED);
    }

    public void markAsFailed() {
        setStatus(ShipmentStatus.FAILED);
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
