package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.UserRole;
import application.domain.models.AuditEntry;

import java.time.Instant;

public class AuditLogResponseDto {
    private String entryId;
    private Instant timestamp;
    private String actorId;
    private UserRole role;
    private String action;
    private String targetAggregateId;
    private String details;

    public AuditLogResponseDto() {}

    public static AuditLogResponseDto fromDomain(AuditEntry entry) {
        if (entry == null) return null;
        AuditLogResponseDto dto = new AuditLogResponseDto();
        dto.setEntryId(entry.getEntryId());
        dto.setTimestamp(entry.getTimestamp());
        dto.setActorId(entry.getActorId());
        dto.setRole(entry.getRole());
        dto.setAction(entry.getAction());
        dto.setTargetAggregateId(entry.getTargetAggregateId());
        dto.setDetails(entry.getDetails());
        return dto;
    }

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
