package application.infrastructure.adapters.out.sql.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class CartEntity {

    @Id
    @Column(name = "cart_id", nullable = false, unique = true)
    private String cartId;

    @Column(name = "buyer_id", nullable = false, unique = true)
    private String buyerId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    private List<CartItemEntity> items = new ArrayList<>();

    public CartEntity() {}

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public List<CartItemEntity> getItems() { return items; }
    public void setItems(List<CartItemEntity> items) { this.items = items; }
}
