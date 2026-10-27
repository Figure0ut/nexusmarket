package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.Shipment;
import application.domain.services.*;
import application.domain.valueobjects.Address;
import application.infrastructure.adapters.in.rest.dto.DispatchShipmentRequestDto;
import application.infrastructure.adapters.in.rest.dto.PrepareShipmentRequestDto;
import application.infrastructure.adapters.in.rest.dto.ShipmentResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final PrepareShipmentService prepareShipmentService;
    private final GetShipmentService getShipmentService;
    private final DispatchShipmentService dispatchShipmentService;
    private final ConfirmDeliveryService confirmDeliveryService;
    private final ReportFailedDeliveryService reportFailedDeliveryService;

    public ShipmentController(PrepareShipmentService prepareShipmentService,
                              GetShipmentService getShipmentService,
                              DispatchShipmentService dispatchShipmentService,
                              ConfirmDeliveryService confirmDeliveryService,
                              ReportFailedDeliveryService reportFailedDeliveryService) {
        this.prepareShipmentService = prepareShipmentService;
        this.getShipmentService = getShipmentService;
        this.dispatchShipmentService = dispatchShipmentService;
        this.confirmDeliveryService = confirmDeliveryService;
        this.reportFailedDeliveryService = reportFailedDeliveryService;
    }

    @PostMapping("/prepare")
    public ResponseEntity<ShipmentResponseDto> prepareShipment(@RequestBody PrepareShipmentRequestDto dto) {
        Shipment shipment = prepareShipmentService.prepareShipment(
                dto.getShipmentId(),
                dto.getOrderId(),
                dto.getWarehouseId(),
                new Address(dto.getDestinationAddress())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ShipmentResponseDto.fromDomain(shipment));
    }

    @GetMapping("/{shipmentId}")
    public ResponseEntity<ShipmentResponseDto> getShipment(@PathVariable String shipmentId) {
        Shipment shipment = getShipmentService.getShipment(shipmentId);
        return ResponseEntity.ok(ShipmentResponseDto.fromDomain(shipment));
    }

    @PutMapping("/{shipmentId}/dispatch")
    public ResponseEntity<Void> dispatchShipment(@PathVariable String shipmentId,
                                                 @RequestBody DispatchShipmentRequestDto dto) {
        dispatchShipmentService.dispatchShipment(shipmentId, dto.getCarrier(), dto.getTrackingNumber());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{shipmentId}/confirm-delivery")
    public ResponseEntity<Void> confirmDelivery(@PathVariable String shipmentId) {
        confirmDeliveryService.confirmDelivery(shipmentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{shipmentId}/report-failed")
    public ResponseEntity<Void> reportFailedDelivery(@PathVariable String shipmentId) {
        reportFailedDeliveryService.reportFailedDelivery(shipmentId);
        return ResponseEntity.noContent().build();
    }
}
