package application.infrastructure.adapters.out.sql.entity;

import application.domain.enums.WarehouseType;
import jakarta.persistence.*;

@Entity
@Table(name = "warehouses")
public class WarehouseEntity {

    @Id
    @Column(name = "warehouse_id", nullable = false, unique = true)
    private String warehouseId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location", nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private WarehouseType type;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "active", nullable = false)
    private boolean active;

    public WarehouseEntity() {}

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
