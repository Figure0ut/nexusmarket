package application.domain.enums;

/**
 * Order lifecycle (Domain 7) exactly as defined by the specification:
 * <ol>
 *   <li>CART - provisional selection of products (represented by the {@code Cart} aggregate)</li>
 *   <li>PENDING_PAYMENT - waiting for financial confirmation</li>
 *   <li>PAID - preparation processes start</li>
 *   <li>DISPATCHED - physical exit from the warehouse</li>
 *   <li>DELIVERED_FINALIZED - successful conclusion of the delivery; the order can no longer be modified</li>
 * </ol>
 */
public enum OrderStatus {
    CART,
    PENDING_PAYMENT,
    PAID,
    DISPATCHED,
    DELIVERED_FINALIZED
}
