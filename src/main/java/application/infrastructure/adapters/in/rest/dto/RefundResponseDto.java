package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.RefundStatus;
import application.domain.models.Refund;

public class RefundResponseDto {
    private String refundId;
    private String returnId;
    private String buyerId;
    private double amount;
    private RefundStatus status;

    public RefundResponseDto() {}

    public static RefundResponseDto fromDomain(Refund refund) {
        if (refund == null) return null;
        RefundResponseDto dto = new RefundResponseDto();
        dto.setRefundId(refund.getRefundId());
        dto.setReturnId(refund.getReturnId());
        dto.setBuyerId(refund.getBuyerId());
        dto.setAmount(refund.getAmount() != null ? refund.getAmount().getAmount().doubleValue() : 0.0);
        dto.setStatus(refund.getStatus());
        return dto;
    }

    public String getRefundId() { return refundId; }
    public void setRefundId(String refundId) { this.refundId = refundId; }

    public String getReturnId() { return returnId; }
    public void setReturnId(String returnId) { this.returnId = returnId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }
}
