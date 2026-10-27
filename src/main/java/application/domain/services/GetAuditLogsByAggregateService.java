package application.domain.services;

import application.domain.models.AuditEntry;
import application.domain.ports.out.AuditLogRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for querying audit logs for a specific aggregate.
 */
public class GetAuditLogsByAggregateService {

    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public GetAuditLogsByAggregateService(AuditLogRepositoryPort auditLogRepositoryPort) {
        if (auditLogRepositoryPort == null) {
            throw new IllegalArgumentException("AuditLogRepositoryPort cannot be null.");
        }
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    public List<AuditEntry> getLogsForAggregate(String aggregateId) {
        if (aggregateId == null || aggregateId.trim().isEmpty()) {
            throw new IllegalArgumentException("Aggregate ID cannot be null or empty.");
        }
        return auditLogRepositoryPort.findByTargetAggregateId(aggregateId.trim());
    }
}
