package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.ShipmentStatus;
import application.domain.models.Shipment;

public class ShipmentResponseDto {
    private String shipmentId;
    private String orderId;
    private String warehouseId;
    private String destinationAddress;
    private String carrier;
    private String trackingNumber;
    private ShipmentStatus status;

    public ShipmentResponseDto() {}

    public static ShipmentResponseDto fromDomain(Shipment shipment) {
        if (shipment == null) return null;
        ShipmentResponseDto dto = new ShipmentResponseDto();
        dto.setShipmentId(shipment.getShipmentId());
        dto.setOrderId(shipment.getOrderId());
        dto.setWarehouseId(shipment.getWarehouseId());
        dto.setDestinationAddress(shipment.getDestinationAddress() != null ? shipment.getDestinationAddress().getStreet() : null);
        dto.setCarrier(shipment.getCarrier());
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setStatus(shipment.getStatus());
        return dto;
    }

    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }
}
