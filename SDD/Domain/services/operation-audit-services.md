# Operation Audit Domain Service Specification

## 1. Context & Business Purpose
The **`OperationAuditService`** consolidates operational and administrative audit logs for compliance, query reports, and executive tracking (OBJ-12 & Dominio 12).

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`OperationAuditService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/OperationAuditService.java)
- **Dependencies**: [`AuditLogRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/AuditLogRepositoryPort.java)
- **Associated Models**: [`AuditEntry`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/AuditEntry.java), [`UserRole`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/UserRole.java)

---

## 3. Core Business Invariants & Rules

1. **Immutability of Audit Logs**: Audit records are append-only.
2. **Context Logging**: Every audit log entry records the unique entry ID, actor ID, user role, action string, target aggregate ID, timestamp, and metadata details.

---

## 4. Method Signatures

```java
public void logOperation(String entryId, String actorId, UserRole role, String action, String targetAggregateId, String details);
public List<AuditEntry> getAllLogs();
public List<AuditEntry> getLogsForAggregate(String aggregateId);
```
