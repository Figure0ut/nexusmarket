package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.Cart;
import application.domain.services.*;
import application.domain.valueobjects.Money;
import application.infrastructure.adapters.in.rest.dto.AddToCartRequestDto;
import application.infrastructure.adapters.in.rest.dto.CartResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final GetOrCreateCartService getOrCreateCartService;
    private final AddItemToCartService addItemToCartService;
    private final RemoveItemFromCartService removeItemFromCartService;
    private final ClearCartService clearCartService;

    public CartController(GetOrCreateCartService getOrCreateCartService,
                          AddItemToCartService addItemToCartService,
                          RemoveItemFromCartService removeItemFromCartService,
                          ClearCartService clearCartService) {
        this.getOrCreateCartService = getOrCreateCartService;
        this.addItemToCartService = addItemToCartService;
        this.removeItemFromCartService = removeItemFromCartService;
        this.clearCartService = clearCartService;
    }

    @GetMapping("/{buyerId}")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable String buyerId) {
        Cart cart = getOrCreateCartService.getOrCreateCart(buyerId);
        return ResponseEntity.ok(CartResponseDto.fromDomain(cart));
    }

    @PostMapping("/{buyerId}/items")
    public ResponseEntity<CartResponseDto> addItem(@PathVariable String buyerId,
                                                   @RequestBody AddToCartRequestDto dto) {
        Cart cart = addItemToCartService.addItemToCart(buyerId, dto.getProductId(), new Money(dto.getUnitPrice()), dto.getQuantity());
        return ResponseEntity.ok(CartResponseDto.fromDomain(cart));
    }

    @DeleteMapping("/{buyerId}/items/{productId}")
    public ResponseEntity<CartResponseDto> removeItem(@PathVariable String buyerId,
                                                      @PathVariable String productId) {
        Cart cart = removeItemFromCartService.removeItemFromCart(buyerId, productId);
        return ResponseEntity.ok(CartResponseDto.fromDomain(cart));
    }

    @DeleteMapping("/{buyerId}")
    public ResponseEntity<Void> clearCart(@PathVariable String buyerId) {
        clearCartService.clearCart(buyerId);
        return ResponseEntity.noContent().build();
    }
}
