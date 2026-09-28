package application.domain.ports.out;

import application.domain.models.AuditEntry;

import java.util.List;

public interface AuditLogRepositoryPort {
    void save(AuditEntry entry);
    List<AuditEntry> findAll();
    List<AuditEntry> findByTargetAggregateId(String aggregateId);
}
