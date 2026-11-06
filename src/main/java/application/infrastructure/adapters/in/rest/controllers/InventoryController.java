package application.infrastructure.adapters.in.rest.controllers;

import application.domain.enums.UserRole;
import application.domain.enums.WarehouseType;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.services.AddStockService;
import application.domain.services.GetInventoriesByProductService;
import application.domain.services.GetInventoryService;
import application.domain.services.GetProductService;
import application.domain.services.GetTotalAvailableStockService;
import application.domain.services.GetWarehouseService;
import application.domain.services.MarkStockDamagedService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.infrastructure.adapters.in.rest.dto.InventoryResponseDto;
import application.infrastructure.adapters.in.rest.dto.StockOperationRequestDto;
import application.infrastructure.adapters.in.rest.security.AuditTrail;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Domain 6 (Distributed Inventory). Managed by Sellers (their own products) and Logistics Operators
 * (matrix: "Inventory administration"). Buyers never manage inventories (Domain 2).
 * <p>
 * Manual movements exposed here: INFLOW (add) and ADJUSTMENT (mark damaged).
 * RESERVE happens at checkout, OUTFLOW_SALE at dispatch and RETURN when a return is received.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final AddStockService addStockService;
    private final MarkStockDamagedService markStockDamagedService;
    private final GetInventoryService getInventoryService;
    private final GetInventoriesByProductService getInventoriesByProductService;
    private final GetTotalAvailableStockService getTotalAvailableStockService;
    private final GetProductService getProductService;
    private final GetWarehouseService getWarehouseService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final AuditTrail auditTrail;

    public InventoryController(AddStockService addStockService,
                               MarkStockDamagedService markStockDamagedService,
                               GetInventoryService getInventoryService,
                               GetInventoriesByProductService getInventoriesByProductService,
                               GetTotalAvailableStockService getTotalAvailableStockService,
                               GetProductService getProductService,
                               GetWarehouseService getWarehouseService,
                               ValidateAuthorizationService authorization,
                               AuthenticatedUserResolver authenticatedUserResolver,
                               AuditTrail auditTrail) {
        this.addStockService = addStockService;
        this.markStockDamagedService = markStockDamagedService;
        this.getInventoryService = getInventoryService;
        this.getInventoriesByProductService = getInventoriesByProductService;
        this.getTotalAvailableStockService = getTotalAvailableStockService;
        this.getProductService = getProductService;
        this.getWarehouseService = getWarehouseService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
        this.auditTrail = auditTrail;
    }

    @PostMapping("/add")
    public ResponseEntity<InventoryResponseDto> addStock(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @RequestBody StockOperationRequestDto dto) {
        User actor = authenticatedUserResolver.resolve(email);
        authorizeManagement(actor, dto.productId(), dto.warehouseId());
        Inventory inventory = addStockService.addStock(dto.productId(), dto.warehouseId(), dto.quantity());
        auditTrail.record(actor, "INVENTORY_INFLOW", inventory.getInventoryId(), "Quantity: " + dto.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryResponseDto.fromDomain(inventory));
    }

    @PostMapping("/mark-damaged")
    public ResponseEntity<InventoryResponseDto> markDamaged(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @RequestBody StockOperationRequestDto dto) {
        User actor = authenticatedUserResolver.resolve(email);
        authorizeManagement(actor, dto.productId(), dto.warehouseId());
        Inventory inventory = markStockDamagedService.markStockAsDamaged(dto.productId(), dto.warehouseId(), dto.quantity());
        auditTrail.record(actor, "INVENTORY_ADJUSTMENT", inventory.getInventoryId(), "Damaged quantity: " + dto.quantity());
        return ResponseEntity.ok(InventoryResponseDto.fromDomain(inventory));
    }

    @GetMapping
    public ResponseEntity<InventoryResponseDto> getInventory(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @RequestParam String productId,
                                                             @RequestParam String warehouseId) {
        User actor = authenticatedUserResolver.resolve(email);
        authorizeConsultation(actor, productId);
        return ResponseEntity.ok(InventoryResponseDto.fromDomain(getInventoryService.getInventory(productId, warehouseId)));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponseDto>> getInventoriesByProduct(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String productId) {
        User actor = authenticatedUserResolver.resolve(email);
        authorizeConsultation(actor, productId);
        return ResponseEntity.ok(getInventoriesByProductService.getInventoriesByProduct(productId).stream()
                .map(InventoryResponseDto::fromDomain).toList());
    }

    @GetMapping("/total-available/{productId}")
    public ResponseEntity<Map<String, Object>> getTotalAvailableStock(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String productId) {
        User actor = authenticatedUserResolver.resolve(email);
        authorizeConsultation(actor, productId);
        int total = getTotalAvailableStockService.getTotalAvailableStock(productId);
        return ResponseEntity.ok(Map.of("productId", productId, "totalAvailableStock", total));
    }

    /**
     * A seller only manages the stock of its own products, and never in another seller's warehouse.
     */
    private void authorizeManagement(User actor, String productId, String warehouseId) {
        authorization.validateProcessAuthorization(actor, BusinessProcess.INVENTORY_MANAGEMENT);
        if (actor.getRole() == UserRole.SELLER) {
            Product product = getProductService.getProduct(productId);
            authorization.validateOwnership(actor, product.getSellerId());
            Warehouse warehouse = getWarehouseService.getWarehouse(warehouseId);
            if (warehouse.getType() == WarehouseType.SELLER) {
                authorization.validateOwnership(actor, warehouse.getOwnerId());
            }
        }
    }

    private void authorizeConsultation(User actor, String productId) {
        authorization.validateQueryAuthorization(actor, BusinessProcess.INVENTORY_MANAGEMENT);
        if (actor.getRole() == UserRole.SELLER) {
            authorization.validateOwnership(actor, getProductService.getProduct(productId).getSellerId());
        }
    }
}
