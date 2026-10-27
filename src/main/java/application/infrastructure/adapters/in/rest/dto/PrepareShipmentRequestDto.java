package application.infrastructure.adapters.in.rest.dto;

public class PrepareShipmentRequestDto {
    private String shipmentId;
    private String orderId;
    private String warehouseId;
    private String destinationAddress;

    public PrepareShipmentRequestDto() {}

    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }
}
