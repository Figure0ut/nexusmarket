package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.Invoice;
import application.domain.services.*;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.TaxIdentifier;
import application.infrastructure.adapters.in.rest.dto.CreateInvoiceRequestDto;
import application.infrastructure.adapters.in.rest.dto.InvoiceResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final CreateInvoiceService createInvoiceService;
    private final GetInvoiceService getInvoiceService;
    private final MarkInvoicePaidService markInvoicePaidService;
    private final CancelInvoiceService cancelInvoiceService;

    public InvoiceController(CreateInvoiceService createInvoiceService,
                             GetInvoiceService getInvoiceService,
                             MarkInvoicePaidService markInvoicePaidService,
                             CancelInvoiceService cancelInvoiceService) {
        this.createInvoiceService = createInvoiceService;
        this.getInvoiceService = getInvoiceService;
        this.markInvoicePaidService = markInvoicePaidService;
        this.cancelInvoiceService = cancelInvoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponseDto> createInvoice(@RequestBody CreateInvoiceRequestDto dto) {
        Invoice invoice = createInvoiceService.createInvoice(
                dto.getInvoiceId(),
                dto.getOrderId(),
                dto.getBuyerId(),
                new TaxIdentifier(dto.getTaxIdentifier()),
                new Address(dto.getBillingAddress())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(InvoiceResponseDto.fromDomain(invoice));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponseDto> getInvoice(@PathVariable String invoiceId) {
        Invoice invoice = getInvoiceService.getInvoice(invoiceId);
        return ResponseEntity.ok(InvoiceResponseDto.fromDomain(invoice));
    }

    @PutMapping("/{invoiceId}/pay")
    public ResponseEntity<Void> markInvoicePaid(@PathVariable String invoiceId) {
        markInvoicePaidService.markInvoicePaid(invoiceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{invoiceId}/cancel")
    public ResponseEntity<Void> cancelInvoice(@PathVariable String invoiceId) {
        cancelInvoiceService.cancelInvoice(invoiceId);
        return ResponseEntity.noContent().build();
    }
}
