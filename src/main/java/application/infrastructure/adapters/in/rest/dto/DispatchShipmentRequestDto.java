package application.infrastructure.adapters.in.rest.dto;

public class DispatchShipmentRequestDto {
    private String carrier;
    private String trackingNumber;

    public DispatchShipmentRequestDto() {}

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
}
