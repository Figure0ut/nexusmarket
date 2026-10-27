package application.infrastructure.adapters.in.rest.dto;

public class AddStockRequestDto {
    private String inventoryId;
    private String productId;
    private String warehouseId;
    private int quantity;

    public AddStockRequestDto() {}

    public String getInventoryId() { return inventoryId; }
    public void setInventoryId(String inventoryId) { this.inventoryId = inventoryId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
