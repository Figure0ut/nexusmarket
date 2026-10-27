package application.infrastructure.adapters.in.rest.dto;

public class AddToCartRequestDto {
    private String productId;
    private double unitPrice;
    private int quantity;

    public AddToCartRequestDto() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
