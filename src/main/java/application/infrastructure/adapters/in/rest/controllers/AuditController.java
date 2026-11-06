package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.User;
import application.domain.services.GetAllAuditLogsService;
import application.domain.services.GetAuditLogsByAggregateService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.infrastructure.adapters.in.rest.dto.AuditLogResponseDto;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Traceability: audit trail of every operation (stored in MongoDB). Consulted by the Administrator and the Supervisor.
 */
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final GetAllAuditLogsService getAllAuditLogsService;
    private final GetAuditLogsByAggregateService getAuditLogsByAggregateService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    public AuditController(GetAllAuditLogsService getAllAuditLogsService,
                           GetAuditLogsByAggregateService getAuditLogsByAggregateService,
                           ValidateAuthorizationService authorization,
                           AuthenticatedUserResolver authenticatedUserResolver) {
        this.getAllAuditLogsService = getAllAuditLogsService;
        this.getAuditLogsByAggregateService = getAuditLogsByAggregateService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponseDto>> getAllLogs(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email) {
        authorize(email);
        return ResponseEntity.ok(getAllAuditLogsService.getAllLogs().stream().map(AuditLogResponseDto::fromDomain).toList());
    }

    @GetMapping("/aggregate/{aggregateId}")
    public ResponseEntity<List<AuditLogResponseDto>> getLogsForAggregate(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String aggregateId) {
        authorize(email);
        return ResponseEntity.ok(getAuditLogsByAggregateService.getLogsForAggregate(aggregateId).stream()
                .map(AuditLogResponseDto::fromDomain).toList());
    }

    private void authorize(String email) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateQueryAuthorization(actor, BusinessProcess.ADMINISTRATIVE_REPORTS);
    }
}
