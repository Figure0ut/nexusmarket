package com.nexusmarket.domain.model.warehouse;

import com.nexusmarket.domain.model.common.Address;

import java.util.Objects;

/**
 * Pure Java Domain Aggregate Root representing a Warehouse in NexusMarket.
 */
public class Warehouse {

    private final String warehouseId;
    private String name;
    private Address location;
    private final WarehouseType type;
    private final String ownerId;
    private boolean active;

    public Warehouse(String warehouseId, String name, Address location, WarehouseType type, String ownerId, boolean active) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse Id cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse name cannot be null or empty.");
        }
        if (location == null) {
            throw new IllegalArgumentException("Warehouse location address cannot be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Warehouse type cannot be null.");
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse owner Id cannot be null or empty.");
        }

        this.warehouseId = warehouseId.trim();
        this.name = name.trim();
        this.location = location;
        this.type = type;
        this.ownerId = ownerId.trim();
        this.active = active;
    }

    public Warehouse(String warehouseId, String name, String locationStr, WarehouseType type, String ownerId) {
        this(warehouseId, name, new Address(locationStr), type, ownerId, true);
    }

    public void updateLocation(Address newLocation) {
        if (newLocation == null) {
            throw new IllegalArgumentException("New location cannot be null.");
        }
        this.location = newLocation;
    }

    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New warehouse name cannot be null or empty.");
        }
        this.name = newName.trim();
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getName() {
        return name;
    }

    public Address getLocation() {
        return location;
    }

    public WarehouseType getType() {
        return type;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public boolean isActive() {
        return active;
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
