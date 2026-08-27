# Operation Audit Domain Service Specification

## 1. Context & Business Purpose
The **Operation Audit Domain Service** consolidates operational and administrative audit logs for compliance, query reports, and executive tracking (OBJ-12 & Dominio 12).

---

## 2. Domain Organizations & Package Location
- **Package**: `com.nexusmarket.domain.service.audit`
- **Associated Value Objects**: `AuditEntry`, `Timestamp`, `ActorId`

---

## 3. Core Business Invariants & Rules

1. **Immutability of Audit Logs**: Audit records are append-only and cannot be altered or deleted.
2. **Comprehensive Context**: Every audit log entry records the timestamp, actor ID, role, action type, and affected aggregate ID.
