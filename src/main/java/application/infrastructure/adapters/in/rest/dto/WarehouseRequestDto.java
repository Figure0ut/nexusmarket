package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.WarehouseType;

/**
 * Warehouse registration. ownerId is ignored for MARKETPLACE warehouses and mandatory for SELLER warehouses.
 */
public record WarehouseRequestDto(String warehouseId, String name, String location, WarehouseType type, String ownerId) {
}
