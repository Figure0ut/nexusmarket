package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.services.*;
import application.domain.valueobjects.Address;
import application.infrastructure.adapters.in.rest.dto.UpdateLocationRequestDto;
import application.infrastructure.adapters.in.rest.dto.WarehouseRequestDto;
import application.infrastructure.adapters.in.rest.dto.WarehouseResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final RegisterWarehouseService registerWarehouseService;
    private final GetWarehouseService getWarehouseService;
    private final ActivateWarehouseService activateWarehouseService;
    private final DeactivateWarehouseService deactivateWarehouseService;
    private final UpdateWarehouseLocationService updateWarehouseLocationService;
    private final GetWarehousesByOwnerService getWarehousesByOwnerService;
    private final GetUserService getUserService;

    public WarehouseController(RegisterWarehouseService registerWarehouseService,
                               GetWarehouseService getWarehouseService,
                               ActivateWarehouseService activateWarehouseService,
                               DeactivateWarehouseService deactivateWarehouseService,
                               UpdateWarehouseLocationService updateWarehouseLocationService,
                               GetWarehousesByOwnerService getWarehousesByOwnerService,
                               GetUserService getUserService) {
        this.registerWarehouseService = registerWarehouseService;
        this.getWarehouseService = getWarehouseService;
        this.activateWarehouseService = activateWarehouseService;
        this.deactivateWarehouseService = deactivateWarehouseService;
        this.updateWarehouseLocationService = updateWarehouseLocationService;
        this.getWarehousesByOwnerService = getWarehousesByOwnerService;
        this.getUserService = getUserService;
    }

    @PostMapping
    public ResponseEntity<WarehouseResponseDto> registerWarehouse(@RequestBody WarehouseRequestDto dto) {
        User admin = getUserService.getUser(dto.getAdminIdentifier());
        Warehouse warehouse = new Warehouse(
                dto.getWarehouseId(),
                dto.getName(),
                new Address(dto.getLocation()),
                dto.getType(),
                dto.getOwnerId(),
                dto.isActive()
        );
        Warehouse registered = registerWarehouseService.registerWarehouse(warehouse, admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(WarehouseResponseDto.fromDomain(registered));
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<WarehouseResponseDto> getWarehouse(@PathVariable String warehouseId) {
        Warehouse warehouse = getWarehouseService.getWarehouse(warehouseId);
        return ResponseEntity.ok(WarehouseResponseDto.fromDomain(warehouse));
    }

    @PutMapping("/{warehouseId}/activate")
    public ResponseEntity<Void> activateWarehouse(@PathVariable String warehouseId) {
        activateWarehouseService.activateWarehouse(warehouseId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{warehouseId}/deactivate")
    public ResponseEntity<Void> deactivateWarehouse(@PathVariable String warehouseId) {
        deactivateWarehouseService.deactivateWarehouse(warehouseId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{warehouseId}/location")
    public ResponseEntity<Void> updateLocation(@PathVariable String warehouseId,
                                               @RequestBody UpdateLocationRequestDto dto) {
        updateWarehouseLocationService.updateWarehouseLocation(warehouseId, new Address(dto.getNewLocation()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<WarehouseResponseDto>> getWarehousesByOwner(@PathVariable String ownerId) {
        List<Warehouse> warehouses = getWarehousesByOwnerService.getWarehousesByOwner(ownerId);
        List<WarehouseResponseDto> dtos = warehouses.stream().map(WarehouseResponseDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }
}
