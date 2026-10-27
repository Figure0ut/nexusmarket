package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.WarehouseType;

public class WarehouseRequestDto {
    private String warehouseId;
    private String name;
    private String location;
    private WarehouseType type;
    private String ownerId;
    private boolean active = true;
    private String adminIdentifier;

    public WarehouseRequestDto() {}

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

    public String getAdminIdentifier() { return adminIdentifier; }
    public void setAdminIdentifier(String adminIdentifier) { this.adminIdentifier = adminIdentifier; }
}
