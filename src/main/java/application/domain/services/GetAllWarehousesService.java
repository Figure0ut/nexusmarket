package application.domain.services;

import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for listing all warehouses (OBJ-04).
 */
public class GetAllWarehousesService {

    private final WarehouseRepositoryPort repositoryPort;

    public GetAllWarehousesService(WarehouseRepositoryPort repositoryPort) {
        if (repositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.repositoryPort = repositoryPort;
    }

    public List<Warehouse> getAllWarehouses() {
        return repositoryPort.findAll();
    }
}
