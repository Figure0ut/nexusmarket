package application.infrastructure.adapters.in.rest.dto;

public class CheckoutRequestDto {
    private String orderId;
    private String shippingAddress;

    public CheckoutRequestDto() {}

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
}
