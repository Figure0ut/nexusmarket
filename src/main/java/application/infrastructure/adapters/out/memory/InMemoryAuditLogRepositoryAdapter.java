package application.infrastructure.adapters.out.memory;

import application.domain.models.AuditEntry;
import application.domain.ports.out.AuditLogRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryAuditLogRepositoryAdapter implements AuditLogRepositoryPort {

    private final List<AuditEntry> storage = new CopyOnWriteArrayList<>();

    @Override
    public void save(AuditEntry entry) {
        storage.add(entry);
    }

    @Override
    public List<AuditEntry> findAll() {
        return List.copyOf(storage);
    }

    @Override
    public List<AuditEntry> findByTargetAggregateId(String aggregateId) {
        return storage.stream()
                .filter(entry -> entry.getTargetAggregateId().equals(aggregateId))
                .toList();
    }
}
