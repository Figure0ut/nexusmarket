package application.infrastructure.adapters.out.sql.entity;

import application.domain.enums.ShipmentStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "shipments")
public class ShipmentEntity {

    @Id
    @Column(name = "shipment_id", nullable = false, unique = true)
    private String shipmentId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "warehouse_id", nullable = false)
    private String warehouseId;

    @Column(name = "destination_address", nullable = false)
    private String destinationAddress;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    public ShipmentEntity() {}

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
