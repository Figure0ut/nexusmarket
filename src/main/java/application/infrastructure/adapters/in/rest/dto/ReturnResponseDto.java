package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.ReturnReason;
import application.domain.enums.ReturnStatus;
import application.domain.models.ReturnRequest;

public class ReturnResponseDto {
    private String returnId;
    private String orderId;
    private String buyerId;
    private String productId;
    private ReturnReason reason;
    private ReturnStatus status;

    public ReturnResponseDto() {}

    public static ReturnResponseDto fromDomain(ReturnRequest request) {
        if (request == null) return null;
        ReturnResponseDto dto = new ReturnResponseDto();
        dto.setReturnId(request.getReturnId());
        dto.setOrderId(request.getOrderId());
        dto.setBuyerId(request.getBuyerId());
        dto.setProductId(request.getProductId());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus());
        return dto;
    }

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

    public ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnStatus status) { this.status = status; }
}
