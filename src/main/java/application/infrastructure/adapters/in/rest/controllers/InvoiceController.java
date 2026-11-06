package application.infrastructure.adapters.in.rest.controllers;

import application.domain.enums.UserRole;
import application.domain.models.Invoice;
import application.domain.models.User;
import application.domain.services.GetInvoiceByOrderService;
import application.domain.services.GetInvoiceService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.infrastructure.adapters.in.rest.dto.InvoiceResponseDto;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Billing (OBJ-09). Invoices are issued automatically when the payment of an order is validated;
 * this controller only exposes consultation. The buyer consults its own invoices; the Administrator and the
 * Supervisor consult them as administrative information.
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final GetInvoiceService getInvoiceService;
    private final GetInvoiceByOrderService getInvoiceByOrderService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    public InvoiceController(GetInvoiceService getInvoiceService,
                             GetInvoiceByOrderService getInvoiceByOrderService,
                             ValidateAuthorizationService authorization,
                             AuthenticatedUserResolver authenticatedUserResolver) {
        this.getInvoiceService = getInvoiceService;
        this.getInvoiceByOrderService = getInvoiceByOrderService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponseDto> getInvoice(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String invoiceId) {
        User actor = authenticatedUserResolver.resolve(email);
        Invoice invoice = getInvoiceService.getInvoice(invoiceId);
        authorize(actor, invoice);
        return ResponseEntity.ok(InvoiceResponseDto.fromDomain(invoice));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<InvoiceResponseDto> getInvoiceByOrder(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String orderId) {
        User actor = authenticatedUserResolver.resolve(email);
        Invoice invoice = getInvoiceByOrderService.getInvoiceByOrder(orderId);
        authorize(actor, invoice);
        return ResponseEntity.ok(InvoiceResponseDto.fromDomain(invoice));
    }

    private void authorize(User actor, Invoice invoice) {
        if (actor.getRole() == UserRole.BUYER) {
            authorization.validateOwnership(actor, invoice.getBuyerId());
        } else {
            authorization.validateQueryAuthorization(actor, BusinessProcess.ADMINISTRATIVE_REPORTS);
        }
    }
}
