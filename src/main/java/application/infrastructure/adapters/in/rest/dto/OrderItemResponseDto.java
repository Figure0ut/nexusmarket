package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.ProductType;

public class OrderItemResponseDto {
    private String productId;
    private String productName;
    private ProductType productType;
    private String warehouseId;
    private double unitPrice;
    private int quantity;
    private double subtotal;

    public OrderItemResponseDto() {}

    public OrderItemResponseDto(String productId, String productName, ProductType productType, String warehouseId,
                                double unitPrice, int quantity, double subtotal) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.warehouseId = warehouseId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
