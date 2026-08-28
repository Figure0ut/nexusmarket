package application.domain.models;

import application.domain.enums.WarehouseType;
import application.domain.valueobjects.Address;

import java.util.Objects;

public class Warehouse {

    private String warehouseId;
    private String name;
    private Address location;
    private WarehouseType type;
    private String ownerId;
    private boolean active;

    public Warehouse() {
    }

    public Warehouse(String warehouseId, String name, Address location, WarehouseType type, String ownerId, boolean active) {
        setWarehouseId(warehouseId);
        setName(name);
        setLocation(location);
        setType(type);
        setOwnerId(ownerId);
        setActive(active);
    }

    public Warehouse(String warehouseId, String name, String locationStr, WarehouseType type, String ownerId) {
        this(warehouseId, name, new Address(locationStr), type, ownerId, true);
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse Id cannot be null or empty.");
        }
        this.warehouseId = warehouseId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        if (location == null) {
            throw new IllegalArgumentException("Warehouse location address cannot be null.");
        }
        this.location = location;
    }

    public WarehouseType getType() {
        return type;
    }

    public void setType(WarehouseType type) {
        if (type == null) {
            throw new IllegalArgumentException("Warehouse type cannot be null.");
        }
        this.type = type;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse owner Id cannot be null or empty.");
        }
        this.ownerId = ownerId.trim();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void updateLocation(Address newLocation) {
        setLocation(newLocation);
    }

    public void updateName(String newName) {
        setName(newName);
    }

    public void deactivate() {
        setActive(false);
    }

    public void activate() {
        setActive(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warehouse warehouse = (Warehouse) o;
        return Objects.equals(warehouseId, warehouse.warehouseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warehouseId);
    }
}
