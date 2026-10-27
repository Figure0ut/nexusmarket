package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.WarehouseType;
import application.domain.models.Warehouse;

public class WarehouseResponseDto {
    private String warehouseId;
    private String name;
    private String location;
    private WarehouseType type;
    private String ownerId;
    private boolean active;

    public WarehouseResponseDto() {}

    public static WarehouseResponseDto fromDomain(Warehouse warehouse) {
        if (warehouse == null) return null;
        WarehouseResponseDto dto = new WarehouseResponseDto();
        dto.setWarehouseId(warehouse.getWarehouseId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation() != null ? warehouse.getLocation().getStreet() : null);
        dto.setType(warehouse.getType());
        dto.setOwnerId(warehouse.getOwnerId());
        dto.setActive(warehouse.isActive());
        return dto;
    }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public WarehouseType getType() { return type; }
    public void setType(WarehouseType type) { this.type = type; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
