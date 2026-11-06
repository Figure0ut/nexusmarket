package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;

import java.util.List;

/**
 * Product registration by a seller. The seller is always the authenticated user.
 */
public record ProductRequestDto(String productId, String sku, String name, String description, double price,
                                ProductType type, ProductStatus status, List<VariantRequestDto> variants) {
}
