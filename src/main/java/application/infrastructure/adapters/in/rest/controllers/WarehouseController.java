package application.infrastructure.adapters.in.rest.controllers;

import application.domain.enums.UserRole;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.services.ActivateWarehouseService;
import application.domain.services.DeactivateWarehouseService;
import application.domain.services.GetAllWarehousesService;
import application.domain.services.GetWarehouseService;
import application.domain.services.GetWarehousesByOwnerService;
import application.domain.services.RegisterWarehouseService;
import application.domain.services.UpdateWarehouseLocationService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.domain.valueobjects.Address;
import application.infrastructure.adapters.in.rest.dto.AddressRequestDto;
import application.infrastructure.adapters.in.rest.dto.WarehouseRequestDto;
import application.infrastructure.adapters.in.rest.dto.WarehouseResponseDto;
import application.infrastructure.adapters.in.rest.security.AuditTrail;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Domain 4 (Warehouse Management). Warehouses are administered by the Administrator (section 5).
 * Sellers and logistics operators can consult them because they operate the inventory stored in them.
 */
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final RegisterWarehouseService registerWarehouseService;
    private final GetWarehouseService getWarehouseService;
    private final GetAllWarehousesService getAllWarehousesService;
    private final ActivateWarehouseService activateWarehouseService;
    private final DeactivateWarehouseService deactivateWarehouseService;
    private final UpdateWarehouseLocationService updateWarehouseLocationService;
    private final GetWarehousesByOwnerService getWarehousesByOwnerService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final AuditTrail auditTrail;

    public WarehouseController(RegisterWarehouseService registerWarehouseService,
                               GetWarehouseService getWarehouseService,
                               GetAllWarehousesService getAllWarehousesService,
                               ActivateWarehouseService activateWarehouseService,
                               DeactivateWarehouseService deactivateWarehouseService,
                               UpdateWarehouseLocationService updateWarehouseLocationService,
                               GetWarehousesByOwnerService getWarehousesByOwnerService,
                               ValidateAuthorizationService authorization,
                               AuthenticatedUserResolver authenticatedUserResolver,
                               AuditTrail auditTrail) {
        this.registerWarehouseService = registerWarehouseService;
        this.getWarehouseService = getWarehouseService;
        this.getAllWarehousesService = getAllWarehousesService;
        this.activateWarehouseService = activateWarehouseService;
        this.deactivateWarehouseService = deactivateWarehouseService;
        this.updateWarehouseLocationService = updateWarehouseLocationService;
        this.getWarehousesByOwnerService = getWarehousesByOwnerService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
        this.auditTrail = auditTrail;
    }

    @PostMapping
    public ResponseEntity<WarehouseResponseDto> registerWarehouse(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @RequestBody WarehouseRequestDto dto) {
        User admin = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(admin, BusinessProcess.WAREHOUSE_MANAGEMENT);
        Warehouse warehouse = new Warehouse(dto.warehouseId(), dto.name(), new Address(dto.location()), dto.type(),
                dto.ownerId(), true);
        Warehouse registered = registerWarehouseService.registerWarehouse(warehouse, admin);
        auditTrail.record(admin, "REGISTER_WAREHOUSE", registered.getWarehouseId(), "Type: " + registered.getType());
        return ResponseEntity.status(HttpStatus.CREATED).body(WarehouseResponseDto.fromDomain(registered));
    }

    @GetMapping
    public ResponseEntity<List<WarehouseResponseDto>> getAllWarehouses(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email) {
        User actor = authenticatedUserResolver.resolve(email);
        validateConsultation(actor);
        List<Warehouse> warehouses = actor.getRole() == UserRole.SELLER
                ? getAllWarehousesService.getAllWarehouses().stream()
                    .filter(w -> w.getType() == application.domain.enums.WarehouseType.MARKETPLACE || w.getOwnerId().equals(actor.getIdentifier()))
                    .toList()
                : getAllWarehousesService.getAllWarehouses();
        return ResponseEntity.ok(warehouses.stream().map(WarehouseResponseDto::fromDomain).toList());
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<WarehouseResponseDto> getWarehouse(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String warehouseId) {
        User actor = authenticatedUserResolver.resolve(email);
        validateConsultation(actor);
        return ResponseEntity.ok(WarehouseResponseDto.fromDomain(getWarehouseService.getWarehouse(warehouseId)));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<WarehouseResponseDto>> getWarehousesByOwner(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String ownerId) {
        User actor = authenticatedUserResolver.resolve(email);
        validateConsultation(actor);
        authorization.validateOwnership(actor, ownerId);
        List<Warehouse> warehouses = getWarehousesByOwnerService.getWarehousesByOwner(ownerId);
        return ResponseEntity.ok(warehouses.stream().map(WarehouseResponseDto::fromDomain).toList());
    }

    @PutMapping("/{warehouseId}/activate")
    public ResponseEntity<WarehouseResponseDto> activateWarehouse(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String warehouseId) {
        User admin = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(admin, BusinessProcess.WAREHOUSE_MANAGEMENT);
        activateWarehouseService.activateWarehouse(warehouseId);
        auditTrail.record(admin, "ACTIVATE_WAREHOUSE", warehouseId, "");
        return ResponseEntity.ok(WarehouseResponseDto.fromDomain(getWarehouseService.getWarehouse(warehouseId)));
    }

    @PutMapping("/{warehouseId}/deactivate")
    public ResponseEntity<WarehouseResponseDto> deactivateWarehouse(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String warehouseId) {
        User admin = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(admin, BusinessProcess.WAREHOUSE_MANAGEMENT);
        deactivateWarehouseService.deactivateWarehouse(warehouseId);
        auditTrail.record(admin, "DEACTIVATE_WAREHOUSE", warehouseId, "");
        return ResponseEntity.ok(WarehouseResponseDto.fromDomain(getWarehouseService.getWarehouse(warehouseId)));
    }

    @PutMapping("/{warehouseId}/location")
    public ResponseEntity<WarehouseResponseDto> updateLocation(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String warehouseId,
                                                               @RequestBody AddressRequestDto dto) {
        User admin = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(admin, BusinessProcess.WAREHOUSE_MANAGEMENT);
        updateWarehouseLocationService.updateWarehouseLocation(warehouseId, new Address(dto.address()));
        auditTrail.record(admin, "UPDATE_WAREHOUSE_LOCATION", warehouseId, dto.address());
        return ResponseEntity.ok(WarehouseResponseDto.fromDomain(getWarehouseService.getWarehouse(warehouseId)));
    }

    private void validateConsultation(User actor) {
        if (actor.getRole() == UserRole.SELLER || actor.getRole() == UserRole.OPERATOR_LOGISTIC) {
            authorization.validateProcessAuthorization(actor, BusinessProcess.INVENTORY_MANAGEMENT);
        } else {
            authorization.validateQueryAuthorization(actor, BusinessProcess.WAREHOUSE_MANAGEMENT);
        }
    }
}
