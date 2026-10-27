package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.AuditEntry;
import application.domain.services.GetAllAuditLogsService;
import application.domain.services.GetAuditLogsByAggregateService;
import application.infrastructure.adapters.in.rest.dto.AuditLogResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final GetAllAuditLogsService getAllAuditLogsService;
    private final GetAuditLogsByAggregateService getAuditLogsByAggregateService;

    public AuditController(GetAllAuditLogsService getAllAuditLogsService,
                           GetAuditLogsByAggregateService getAuditLogsByAggregateService) {
        this.getAllAuditLogsService = getAllAuditLogsService;
        this.getAuditLogsByAggregateService = getAuditLogsByAggregateService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponseDto>> getAllLogs() {
        List<AuditEntry> entries = getAllAuditLogsService.getAllLogs();
        List<AuditLogResponseDto> dtos = entries.stream().map(AuditLogResponseDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/aggregate/{aggregateId}")
    public ResponseEntity<List<AuditLogResponseDto>> getLogsForAggregate(@PathVariable String aggregateId) {
        List<AuditEntry> entries = getAuditLogsByAggregateService.getLogsForAggregate(aggregateId);
        List<AuditLogResponseDto> dtos = entries.stream().map(AuditLogResponseDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }
}
