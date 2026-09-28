package application.domain.models;

import application.domain.enums.UserRole;

import java.time.Instant;
import java.util.Objects;

public class AuditEntry {

    private String entryId;
    private String actorId;
    private UserRole role;
    private String action;
    private String targetAggregateId;
    private Instant timestamp;
    private String details;

    public AuditEntry() {
        this.timestamp = Instant.now();
    }

    public AuditEntry(String entryId, String actorId, UserRole role, String action, String targetAggregateId, String details) {
        setEntryId(entryId);
        setActorId(actorId);
        setRole(role);
        setAction(action);
        setTargetAggregateId(targetAggregateId);
        this.timestamp = Instant.now();
        setDetails(details);
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        if (entryId == null || entryId.trim().isEmpty()) {
            throw new IllegalArgumentException("Audit entry ID cannot be null or empty.");
        }
        this.entryId = entryId.trim();
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        if (actorId == null || actorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Actor ID cannot be null or empty.");
        }
        this.actorId = actorId.trim();
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null.");
        }
        this.role = role;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Audit action cannot be null or empty.");
        }
        this.action = action.trim();
    }

    public String getTargetAggregateId() {
        return targetAggregateId;
    }

    public void setTargetAggregateId(String targetAggregateId) {
        if (targetAggregateId == null || targetAggregateId.trim().isEmpty()) {
            throw new IllegalArgumentException("Target aggregate ID cannot be null or empty.");
        }
        this.targetAggregateId = targetAggregateId.trim();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details != null ? details.trim() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditEntry that = (AuditEntry) o;
        return Objects.equals(entryId, that.entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId);
    }
}
