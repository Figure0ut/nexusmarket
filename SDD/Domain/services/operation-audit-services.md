# Operation Audit Atomic Domain Services Specification

## 1. Context & Business Purpose
The Operation Audit domain records an append-only audit trail for administrative actions, security transitions, and operational traceability (OBJ-12 & Dominio 12). Each operation is implemented as an atomic domain service.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Dependencies**: [`AuditLogRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/AuditLogRepositoryPort.java)
- **Associated Models**: [`AuditEntry`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/AuditEntry.java), [`UserRole`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/UserRole.java)

### Services
1. **`LogOperationService`**: Records an immutable audit log entry.
2. **`GetAllAuditLogsService`**: Retrieves all audit logs recorded in the system.
3. **`GetAuditLogsByAggregateService`**: Retrieves audit logs for a specific target aggregate.

---

## 3. Core Business Invariants & Rules

1. **Append-Only Immutability**: Audit entries are strictly append-only; modification or deletion is disallowed.
2. **Actor Traceability**: Every audit entry records the actor ID and their active role at the time of operation.

---

## 4. Method Signatures

```java
// LogOperationService
public void logOperation(String entryId, String actorId, UserRole role, String action, String targetAggregateId, String details);

// GetAllAuditLogsService
public List<AuditEntry> getAllLogs();

// GetAuditLogsByAggregateService
public List<AuditEntry> getLogsForAggregate(String aggregateId);
```
