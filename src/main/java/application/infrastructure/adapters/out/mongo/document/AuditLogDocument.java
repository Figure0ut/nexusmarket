package application.infrastructure.adapters.out.mongo.document;

import application.domain.enums.UserRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "audit_logs")
public class AuditLogDocument {

    @Id
    private String entryId;

    private Instant timestamp;
    private String actorId;
    private UserRole role;
    private String action;

    @Indexed
    private String targetAggregateId;

    private String details;

    public AuditLogDocument() {}

    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTargetAggregateId() { return targetAggregateId; }
    public void setTargetAggregateId(String targetAggregateId) { this.targetAggregateId = targetAggregateId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
