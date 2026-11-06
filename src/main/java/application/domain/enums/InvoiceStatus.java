package application.domain.enums;

/**
 * An invoice is issued once the payment of an order has been validated (section 6.1, step 6).
 * The specification does not define any further invoice lifecycle.
 */
public enum InvoiceStatus {
    ISSUED
}
