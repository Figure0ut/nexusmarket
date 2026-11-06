package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.Cart;
import application.domain.models.User;
import application.domain.services.AddItemToCartService;
import application.domain.services.ClearCartService;
import application.domain.services.GetOrCreateCartService;
import application.domain.services.RemoveItemFromCartService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.infrastructure.adapters.in.rest.dto.AddToCartRequestDto;
import application.infrastructure.adapters.in.rest.dto.CartResponseDto;
import application.infrastructure.adapters.in.rest.security.AuditTrail;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Shopping cart (OBJ-07): first stage of the order lifecycle ("Cart"). Only the buyer that owns the cart can use it.
 */
@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final GetOrCreateCartService getOrCreateCartService;
    private final AddItemToCartService addItemToCartService;
    private final RemoveItemFromCartService removeItemFromCartService;
    private final ClearCartService clearCartService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final AuditTrail auditTrail;

    public CartController(GetOrCreateCartService getOrCreateCartService,
                          AddItemToCartService addItemToCartService,
                          RemoveItemFromCartService removeItemFromCartService,
                          ClearCartService clearCartService,
                          ValidateAuthorizationService authorization,
                          AuthenticatedUserResolver authenticatedUserResolver,
                          AuditTrail auditTrail) {
        this.getOrCreateCartService = getOrCreateCartService;
        this.addItemToCartService = addItemToCartService;
        this.removeItemFromCartService = removeItemFromCartService;
        this.clearCartService = clearCartService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
        this.auditTrail = auditTrail;
    }

    @GetMapping("/{buyerId}")
    public ResponseEntity<CartResponseDto> getCart(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String buyerId) {
        authorizeBuyer(email, buyerId);
        return ResponseEntity.ok(CartResponseDto.fromDomain(getOrCreateCartService.getOrCreateCart(buyerId)));
    }

    @PostMapping("/{buyerId}/items")
    public ResponseEntity<CartResponseDto> addItem(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String buyerId, @RequestBody AddToCartRequestDto dto) {
        User buyer = authorizeBuyer(email, buyerId);
        Cart cart = addItemToCartService.addItemToCart(buyerId, dto.productId(), dto.quantity());
        auditTrail.record(buyer, "CART_ADD_ITEM", cart.getCartId(), dto.productId() + " x" + dto.quantity());
        return ResponseEntity.ok(CartResponseDto.fromDomain(cart));
    }

    @DeleteMapping("/{buyerId}/items/{productId}")
    public ResponseEntity<CartResponseDto> removeItem(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String buyerId, @PathVariable String productId) {
        User buyer = authorizeBuyer(email, buyerId);
        Cart cart = removeItemFromCartService.removeItemFromCart(buyerId, productId);
        auditTrail.record(buyer, "CART_REMOVE_ITEM", cart.getCartId(), productId);
        return ResponseEntity.ok(CartResponseDto.fromDomain(cart));
    }

    @DeleteMapping("/{buyerId}")
    public ResponseEntity<Void> clearCart(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String buyerId) {
        User buyer = authorizeBuyer(email, buyerId);
        clearCartService.clearCart(buyerId);
        auditTrail.record(buyer, "CART_CLEAR", "CART-" + buyerId, "");
        return ResponseEntity.noContent().build();
    }

    private User authorizeBuyer(String email, String buyerId) {
        User buyer = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(buyer, BusinessProcess.CART_MANAGEMENT);
        authorization.validateOwnership(buyer, buyerId);
        return buyer;
    }
}
