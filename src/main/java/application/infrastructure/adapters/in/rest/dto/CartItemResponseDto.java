package application.infrastructure.adapters.in.rest.dto;

public class CartItemResponseDto {
    private String productId;
    private double unitPrice;
    private int quantity;
    private double subtotal;

    public CartItemResponseDto() {}

    public CartItemResponseDto(String productId, double unitPrice, int quantity, double subtotal) {
        this.productId = productId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
