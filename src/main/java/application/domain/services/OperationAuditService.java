package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.models.AuditEntry;
import application.domain.ports.out.AuditLogRepositoryPort;

import java.util.List;

public class OperationAuditService {

    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public OperationAuditService(AuditLogRepositoryPort auditLogRepositoryPort) {
        if (auditLogRepositoryPort == null) {
            throw new IllegalArgumentException("AuditLogRepositoryPort cannot be null.");
        }
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    public void logOperation(String entryId, String actorId, UserRole role, String action, String targetAggregateId, String details) {
        AuditEntry entry = new AuditEntry(entryId, actorId, role, action, targetAggregateId, details);
        auditLogRepositoryPort.save(entry);
    }

    public List<AuditEntry> getAllLogs() {
        return auditLogRepositoryPort.findAll();
    }

    public List<AuditEntry> getLogsForAggregate(String aggregateId) {
        if (aggregateId == null || aggregateId.trim().isEmpty()) {
            throw new IllegalArgumentException("Aggregate ID cannot be null or empty.");
        }
        return auditLogRepositoryPort.findByTargetAggregateId(aggregateId.trim());
    }
}
