package application.infrastructure.adapters.out.mongo;

import application.domain.models.AuditEntry;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.infrastructure.adapters.out.mongo.document.AuditLogDocument;
import application.infrastructure.adapters.out.mongo.repository.SpringDataMongoAuditLogRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MongoDB Outbound Persistence Adapter for System Audit Logs.
 * Satisfies AuditLogRepositoryPort using Spring Data MongoDB documents.
 */
@Component
@Primary
public class MongoAuditLogRepositoryAdapter implements AuditLogRepositoryPort {

    private final SpringDataMongoAuditLogRepository mongoRepository;

    public MongoAuditLogRepositoryAdapter(SpringDataMongoAuditLogRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public void save(AuditEntry entry) {
        if (entry == null) return;
        AuditLogDocument doc = new AuditLogDocument();
        doc.setEntryId(entry.getEntryId());
        doc.setTimestamp(entry.getTimestamp());
        doc.setActorId(entry.getActorId());
        doc.setRole(entry.getRole());
        doc.setAction(entry.getAction());
        doc.setTargetAggregateId(entry.getTargetAggregateId());
        doc.setDetails(entry.getDetails());
        mongoRepository.save(doc);
    }

    @Override
    public List<AuditEntry> findAll() {
        return mongoRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<AuditEntry> findByTargetAggregateId(String targetAggregateId) {
        if (targetAggregateId == null) return List.of();
        return mongoRepository.findByTargetAggregateId(targetAggregateId).stream().map(this::toDomain).toList();
    }

    private AuditEntry toDomain(AuditLogDocument doc) {
        if (doc == null) return null;
        AuditEntry entry = new AuditEntry(
                doc.getEntryId(),
                doc.getActorId(),
                doc.getRole(),
                doc.getAction(),
                doc.getTargetAggregateId(),
                doc.getDetails()
        );
        entry.setTimestamp(doc.getTimestamp());
        return entry;
    }
}
