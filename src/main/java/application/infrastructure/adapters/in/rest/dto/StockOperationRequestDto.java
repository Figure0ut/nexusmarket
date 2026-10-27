package application.infrastructure.adapters.in.rest.dto;

public class StockOperationRequestDto {
    private String productId;
    private String warehouseId;
    private int quantity;

    public StockOperationRequestDto() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
