package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.services.*;
import application.domain.valueobjects.Money;
import application.infrastructure.adapters.in.rest.dto.ProcessRefundRequestDto;
import application.infrastructure.adapters.in.rest.dto.RefundResponseDto;
import application.infrastructure.adapters.in.rest.dto.ReturnRequestDto;
import application.infrastructure.adapters.in.rest.dto.ReturnResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
public class ReturnRefundController {

    private final RequestReturnService requestReturnService;
    private final GetReturnRequestService getReturnRequestService;
    private final ApproveReturnService approveReturnService;
    private final RejectReturnService rejectReturnService;
    private final ProcessReturnAndRefundService processReturnAndRefundService;

    public ReturnRefundController(RequestReturnService requestReturnService,
                                  GetReturnRequestService getReturnRequestService,
                                  ApproveReturnService approveReturnService,
                                  RejectReturnService rejectReturnService,
                                  ProcessReturnAndRefundService processReturnAndRefundService) {
        this.requestReturnService = requestReturnService;
        this.getReturnRequestService = getReturnRequestService;
        this.approveReturnService = approveReturnService;
        this.rejectReturnService = rejectReturnService;
        this.processReturnAndRefundService = processReturnAndRefundService;
    }

    @PostMapping("/request")
    public ResponseEntity<ReturnResponseDto> requestReturn(@RequestBody ReturnRequestDto dto) {
        ReturnRequest returnRequest = requestReturnService.requestReturn(
                dto.getReturnId(),
                dto.getOrderId(),
                dto.getBuyerId(),
                dto.getProductId(),
                dto.getReason()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ReturnResponseDto.fromDomain(returnRequest));
    }

    @GetMapping("/{returnId}")
    public ResponseEntity<ReturnResponseDto> getReturn(@PathVariable String returnId) {
        ReturnRequest returnRequest = getReturnRequestService.getReturnRequest(returnId);
        return ResponseEntity.ok(ReturnResponseDto.fromDomain(returnRequest));
    }

    @PutMapping("/{returnId}/approve")
    public ResponseEntity<Void> approveReturn(@PathVariable String returnId) {
        approveReturnService.approveReturn(returnId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{returnId}/reject")
    public ResponseEntity<Void> rejectReturn(@PathVariable String returnId) {
        rejectReturnService.rejectReturn(returnId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/process-refund")
    public ResponseEntity<RefundResponseDto> processRefund(@RequestBody ProcessRefundRequestDto dto) {
        Refund refund = processReturnAndRefundService.processReturnedItemAndRefund(
                dto.getRefundId(),
                dto.getReturnId(),
                dto.getWarehouseId(),
                dto.isReusable(),
                new Money(dto.getRefundAmount())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(RefundResponseDto.fromDomain(refund));
    }
}
