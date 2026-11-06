package application.domain.enums;

/**
 * Catalog status of a product (Domain 5).
 * The specification defines exactly three values: Published, Suspended and Discontinued.
 * Only PUBLISHED products are visible in the public catalog.
 */
public enum ProductStatus {
    PUBLISHED,
    SUSPENDED,
    DISCONTINUED
}
