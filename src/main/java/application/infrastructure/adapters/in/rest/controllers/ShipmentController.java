package application.infrastructure.adapters.in.rest.controllers;

import application.domain.enums.UserRole;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.services.ConfirmDeliveryService;
import application.domain.services.DispatchShipmentService;
import application.domain.services.GetOrderService;
import application.domain.services.GetShipmentService;
import application.domain.services.PrepareShipmentService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.infrastructure.adapters.in.rest.dto.DispatchShipmentRequestDto;
import application.infrastructure.adapters.in.rest.dto.PrepareShipmentRequestDto;
import application.infrastructure.adapters.in.rest.dto.ShipmentResponseDto;
import application.infrastructure.adapters.in.rest.security.AuditTrail;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Logistics (OBJ-10): packing, dispatch and transport of physical products, executed by the Logistics Operator
 * ("in charge of the physical operation of warehouses and dispatches").
 */
@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final PrepareShipmentService prepareShipmentService;
    private final GetShipmentService getShipmentService;
    private final DispatchShipmentService dispatchShipmentService;
    private final ConfirmDeliveryService confirmDeliveryService;
    private final GetOrderService getOrderService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final AuditTrail auditTrail;

    public ShipmentController(PrepareShipmentService prepareShipmentService,
                              GetShipmentService getShipmentService,
                              DispatchShipmentService dispatchShipmentService,
                              ConfirmDeliveryService confirmDeliveryService,
                              GetOrderService getOrderService,
                              ValidateAuthorizationService authorization,
                              AuthenticatedUserResolver authenticatedUserResolver,
                              AuditTrail auditTrail) {
        this.prepareShipmentService = prepareShipmentService;
        this.getShipmentService = getShipmentService;
        this.dispatchShipmentService = dispatchShipmentService;
        this.confirmDeliveryService = confirmDeliveryService;
        this.getOrderService = getOrderService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
        this.auditTrail = auditTrail;
    }

    @PostMapping("/prepare")
    public ResponseEntity<ShipmentResponseDto> prepareShipment(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @RequestBody PrepareShipmentRequestDto dto) {
        User operator = authorizeOperator(email);
        Shipment shipment = prepareShipmentService.prepareShipment(dto.shipmentId(), dto.orderId());
        auditTrail.record(operator, "PREPARE_SHIPMENT", shipment.getShipmentId(), "Order: " + dto.orderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ShipmentResponseDto.fromDomain(shipment));
    }

    @GetMapping("/{shipmentId}")
    public ResponseEntity<ShipmentResponseDto> getShipment(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String shipmentId) {
        User actor = authenticatedUserResolver.resolve(email);
        Shipment shipment = getShipmentService.getShipment(shipmentId);
        if (actor.getRole() == UserRole.BUYER) {
            authorization.validateOwnership(actor, getOrderService.getOrder(shipment.getOrderId()).getBuyerId());
        } else {
            authorization.validateQueryAuthorization(actor, BusinessProcess.SHIPMENT_MANAGEMENT);
        }
        return ResponseEntity.ok(ShipmentResponseDto.fromDomain(shipment));
    }

    @PutMapping("/{shipmentId}/dispatch")
    public ResponseEntity<ShipmentResponseDto> dispatchShipment(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String shipmentId,
                                                                @RequestBody DispatchShipmentRequestDto dto) {
        User operator = authorizeOperator(email);
        Shipment shipment = dispatchShipmentService.dispatchShipment(shipmentId, dto.carrier(), dto.trackingNumber());
        auditTrail.record(operator, "DISPATCH_SHIPMENT", shipmentId, "Carrier: " + dto.carrier() + ", tracking: " + dto.trackingNumber());
        return ResponseEntity.ok(ShipmentResponseDto.fromDomain(shipment));
    }

    @PutMapping("/{shipmentId}/confirm-delivery")
    public ResponseEntity<ShipmentResponseDto> confirmDelivery(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String shipmentId) {
        User operator = authorizeOperator(email);
        Shipment shipment = confirmDeliveryService.confirmDelivery(shipmentId);
        auditTrail.record(operator, "CONFIRM_DELIVERY", shipmentId, "Order: " + shipment.getOrderId() + " DELIVERED_FINALIZED");
        return ResponseEntity.ok(ShipmentResponseDto.fromDomain(shipment));
    }

    private User authorizeOperator(String email) {
        User operator = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(operator, BusinessProcess.SHIPMENT_MANAGEMENT);
        return operator;
    }
}
