package application.infrastructure.adapters.in.rest.dto;

import application.domain.models.Cart;
import application.domain.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartResponseDto {
    private String cartId;
    private String buyerId;
    private String status;
    private List<CartItemResponseDto> items = new ArrayList<>();
    private double total;

    public CartResponseDto() {}

    public static CartResponseDto fromDomain(Cart cart) {
        if (cart == null) return null;
        CartResponseDto dto = new CartResponseDto();
        dto.setCartId(cart.getCartId());
        dto.setBuyerId(cart.getBuyerId());
        dto.setStatus(cart.getStatus().name());
        dto.setTotal(cart.calculateTotal() != null ? cart.calculateTotal().getAmount().doubleValue() : 0.0);

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                dto.getItems().add(new CartItemResponseDto(
                        item.getProductId(),
                        item.getUnitPrice() != null ? item.getUnitPrice().getAmount().doubleValue() : 0.0,
                        item.getQuantity(),
                        item.calculateSubtotal() != null ? item.calculateSubtotal().getAmount().doubleValue() : 0.0
                ));
            }
        }
        return dto;
    }

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public List<CartItemResponseDto> getItems() { return items; }
    public void setItems(List<CartItemResponseDto> items) { this.items = items; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
