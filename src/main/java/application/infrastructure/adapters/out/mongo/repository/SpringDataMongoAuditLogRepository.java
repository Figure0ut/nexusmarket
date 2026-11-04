package application.infrastructure.adapters.out.mongo.repository;

import application.infrastructure.adapters.out.mongo.document.AuditLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMongoAuditLogRepository extends MongoRepository<AuditLogDocument, String> {
    List<AuditLogDocument> findByTargetAggregateId(String targetAggregateId);
}
