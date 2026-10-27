package application.domain.services;

import application.domain.models.AuditEntry;
import application.domain.ports.out.AuditLogRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for querying all system audit logs.
 */
public class GetAllAuditLogsService {

    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public GetAllAuditLogsService(AuditLogRepositoryPort auditLogRepositoryPort) {
        if (auditLogRepositoryPort == null) {
            throw new IllegalArgumentException("AuditLogRepositoryPort cannot be null.");
        }
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    public List<AuditEntry> getAllLogs() {
        return auditLogRepositoryPort.findAll();
    }
}
