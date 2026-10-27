package application.infrastructure.adapters.in.rest.dto;

public class ProcessRefundRequestDto {
    private String refundId;
    private String returnId;
    private String warehouseId;
    private boolean reusable;
    private double refundAmount;

    public ProcessRefundRequestDto() {}

    public String getRefundId() { return refundId; }
    public void setRefundId(String refundId) { this.refundId = refundId; }

    public String getReturnId() { return returnId; }
    public void setReturnId(String returnId) { this.returnId = returnId; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public boolean isReusable() { return reusable; }
    public void setReusable(boolean reusable) { this.reusable = reusable; }

    public double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(double refundAmount) { this.refundAmount = refundAmount; }
}
