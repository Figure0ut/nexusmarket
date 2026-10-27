package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.ReturnReason;

public class ReturnRequestDto {
    private String returnId;
    private String orderId;
    private String buyerId;
    private String productId;
    private ReturnReason reason;

    public ReturnRequestDto() {}

    public String getReturnId() { return returnId; }
    public void setReturnId(String returnId) { this.returnId = returnId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public ReturnReason getReason() { return reason; }
    public void setReason(ReturnReason reason) { this.reason = reason; }
}
