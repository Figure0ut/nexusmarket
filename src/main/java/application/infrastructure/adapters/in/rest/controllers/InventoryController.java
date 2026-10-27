package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.Inventory;
import application.domain.services.*;
import application.infrastructure.adapters.in.rest.dto.AddStockRequestDto;
import application.infrastructure.adapters.in.rest.dto.InventoryResponseDto;
import application.infrastructure.adapters.in.rest.dto.StockOperationRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final AddStockService addStockService;
    private final ReserveStockService reserveStockService;
    private final ReleaseStockReservationService releaseStockReservationService;
    private final ConfirmSaleStockService confirmSaleStockService;
    private final MarkStockDamagedService markStockDamagedService;
    private final GetInventoryService getInventoryService;
    private final GetTotalAvailableStockService getTotalAvailableStockService;

    public InventoryController(AddStockService addStockService,
                               ReserveStockService reserveStockService,
                               ReleaseStockReservationService releaseStockReservationService,
                               ConfirmSaleStockService confirmSaleStockService,
                               MarkStockDamagedService markStockDamagedService,
                               GetInventoryService getInventoryService,
                               GetTotalAvailableStockService getTotalAvailableStockService) {
        this.addStockService = addStockService;
        this.reserveStockService = reserveStockService;
        this.releaseStockReservationService = releaseStockReservationService;
        this.confirmSaleStockService = confirmSaleStockService;
        this.markStockDamagedService = markStockDamagedService;
        this.getInventoryService = getInventoryService;
        this.getTotalAvailableStockService = getTotalAvailableStockService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addStock(@RequestBody AddStockRequestDto dto) {
        addStockService.addStock(dto.getInventoryId(), dto.getProductId(), dto.getWarehouseId(), dto.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<InventoryResponseDto> getInventory(@RequestParam String productId,
                                                            @RequestParam String warehouseId) {
        Inventory inventory = getInventoryService.getInventory(productId, warehouseId);
        return ResponseEntity.ok(InventoryResponseDto.fromDomain(inventory));
    }

    @GetMapping("/total-available/{productId}")
    public ResponseEntity<Map<String, Object>> getTotalAvailableStock(@PathVariable String productId) {
        int total = getTotalAvailableStockService.getTotalAvailableStock(productId);
        return ResponseEntity.ok(Map.of("productId", productId, "totalAvailableStock", total));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(@RequestBody StockOperationRequestDto dto) {
        reserveStockService.reserveStock(dto.getProductId(), dto.getWarehouseId(), dto.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseReservation(@RequestBody StockOperationRequestDto dto) {
        releaseStockReservationService.releaseReservation(dto.getProductId(), dto.getWarehouseId(), dto.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/confirm-sale")
    public ResponseEntity<Void> confirmSale(@RequestBody StockOperationRequestDto dto) {
        confirmSaleStockService.confirmSale(dto.getProductId(), dto.getWarehouseId(), dto.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/mark-damaged")
    public ResponseEntity<Void> markDamaged(@RequestBody StockOperationRequestDto dto) {
        markStockDamagedService.markStockAsDamaged(dto.getProductId(), dto.getWarehouseId(), dto.getQuantity());
        return ResponseEntity.noContent().build();
    }
}
