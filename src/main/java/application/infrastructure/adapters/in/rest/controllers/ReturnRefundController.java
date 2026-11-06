package application.infrastructure.adapters.in.rest.controllers;

import application.domain.enums.UserRole;
import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.models.User;
import application.domain.services.ApproveReturnService;
import application.domain.services.GetReturnRequestService;
import application.domain.services.ProcessReturnAndRefundService;
import application.domain.services.RejectReturnService;
import application.domain.services.RequestReturnService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.infrastructure.adapters.in.rest.dto.ProcessRefundRequestDto;
import application.infrastructure.adapters.in.rest.dto.RefundResponseDto;
import application.infrastructure.adapters.in.rest.dto.ReturnRequestDto;
import application.infrastructure.adapters.in.rest.dto.ReturnResponseDto;
import application.infrastructure.adapters.in.rest.security.AuditTrail;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Returns and refunds (OBJ-11). Matrix "Refund management": Buyer and Administrator.
 * The buyer requests the return of a product of its delivered order; the Administrator approves/rejects it
 * and processes the refund.
 */
@RestController
@RequestMapping("/api/returns")
public class ReturnRefundController {

    private final RequestReturnService requestReturnService;
    private final GetReturnRequestService getReturnRequestService;
    private final ApproveReturnService approveReturnService;
    private final RejectReturnService rejectReturnService;
    private final ProcessReturnAndRefundService processReturnAndRefundService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final AuditTrail auditTrail;

    public ReturnRefundController(RequestReturnService requestReturnService,
                                  GetReturnRequestService getReturnRequestService,
                                  ApproveReturnService approveReturnService,
                                  RejectReturnService rejectReturnService,
                                  ProcessReturnAndRefundService processReturnAndRefundService,
                                  ValidateAuthorizationService authorization,
                                  AuthenticatedUserResolver authenticatedUserResolver,
                                  AuditTrail auditTrail) {
        this.requestReturnService = requestReturnService;
        this.getReturnRequestService = getReturnRequestService;
        this.approveReturnService = approveReturnService;
        this.rejectReturnService = rejectReturnService;
        this.processReturnAndRefundService = processReturnAndRefundService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
        this.auditTrail = auditTrail;
    }

    @PostMapping("/request")
    public ResponseEntity<ReturnResponseDto> requestReturn(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @RequestBody ReturnRequestDto dto) {
        User buyer = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(buyer, BusinessProcess.REFUND_MANAGEMENT);
        if (buyer.getRole() != UserRole.BUYER) {
            throw new application.domain.exceptions.AuthorizationException("Only the buyer of the order can request a return.");
        }
        ReturnRequest returnRequest = requestReturnService.requestReturn(dto.returnId(), dto.orderId(),
                buyer.getIdentifier(), dto.productId(), dto.reason());
        auditTrail.record(buyer, "REQUEST_RETURN", returnRequest.getReturnId(), "Order: " + dto.orderId() + ", product: " + dto.productId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ReturnResponseDto.fromDomain(returnRequest));
    }

    @GetMapping("/{returnId}")
    public ResponseEntity<ReturnResponseDto> getReturn(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String returnId) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateQueryAuthorization(actor, BusinessProcess.REFUND_MANAGEMENT);
        ReturnRequest returnRequest = getReturnRequestService.getReturnRequest(returnId);
        authorization.validateOwnership(actor, returnRequest.getBuyerId());
        return ResponseEntity.ok(ReturnResponseDto.fromDomain(returnRequest));
    }

    @PutMapping("/{returnId}/approve")
    public ResponseEntity<ReturnResponseDto> approveReturn(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String returnId) {
        User admin = authorizeAdmin(email);
        approveReturnService.approveReturn(returnId);
        auditTrail.record(admin, "APPROVE_RETURN", returnId, "");
        return ResponseEntity.ok(ReturnResponseDto.fromDomain(getReturnRequestService.getReturnRequest(returnId)));
    }

    @PutMapping("/{returnId}/reject")
    public ResponseEntity<ReturnResponseDto> rejectReturn(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String returnId) {
        User admin = authorizeAdmin(email);
        rejectReturnService.rejectReturn(returnId);
        auditTrail.record(admin, "REJECT_RETURN", returnId, "");
        return ResponseEntity.ok(ReturnResponseDto.fromDomain(getReturnRequestService.getReturnRequest(returnId)));
    }

    @PostMapping("/process-refund")
    public ResponseEntity<RefundResponseDto> processRefund(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @RequestBody ProcessRefundRequestDto dto) {
        User admin = authorizeAdmin(email);
        Refund refund = processReturnAndRefundService.processReturnedItemAndRefund(dto.refundId(), dto.returnId(), dto.reusable());
        auditTrail.record(admin, "PROCESS_REFUND", refund.getRefundId(), "Return: " + dto.returnId() + ", amount: " + refund.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(RefundResponseDto.fromDomain(refund));
    }

    private User authorizeAdmin(String email) {
        User admin = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(admin, BusinessProcess.REFUND_MANAGEMENT);
        if (admin.getRole() != UserRole.ADMIN) {
            throw new application.domain.exceptions.AuthorizationException("Only the Administrator approves returns and processes refunds.");
        }
        return admin;
    }
}
