package application.domain.enums;

/**
 * Logistics stages of a shipment for physical products (section 6.1, step 7: packing, dispatch and transport).
 */
public enum ShipmentStatus {
    PREPARING,
    IN_TRANSIT,
    DELIVERED
}
