package application.infrastructure.adapters.in.rest.dto;

/**
 * shippingAddress is optional: when omitted, the main address of the buyer is used.
 */
public record CheckoutRequestDto(String orderId, String shippingAddress) {
}
