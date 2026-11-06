package application.infrastructure.adapters.in.rest.dto;

/**
 * The unit price is never sent by the client: it is always taken from the catalog.
 */
public record AddToCartRequestDto(String productId, int quantity) {
}
