package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.models.AuditEntry;
import application.domain.ports.out.AuditLogRepositoryPort;

/**
 * Atomic domain service responsible exclusively for recording an audit entry.
 */
public class LogOperationService {

    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public LogOperationService(AuditLogRepositoryPort auditLogRepositoryPort) {
        if (auditLogRepositoryPort == null) {
            throw new IllegalArgumentException("AuditLogRepositoryPort cannot be null.");
        }
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    public void logOperation(String entryId, String actorId, UserRole role, String action, String targetAggregateId, String details) {
        AuditEntry entry = new AuditEntry(entryId, actorId, role, action, targetAggregateId, details);
        auditLogRepositoryPort.save(entry);
    }
}
