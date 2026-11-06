# Audit & Administrative Reports Services Specification

## 1. Business Purpose
Traceability (section 1) and OBJ-12 "Consolidate administrative information for consultation".

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`LogOperationService`](../../../src/main/java/application/domain/services/LogOperationService.java) | Records an `AuditEntry` (stored in MongoDB). Called by `AuditTrail` after every successful state-changing request. |
| [`GetAllAuditLogsService`](../../../src/main/java/application/domain/services/GetAllAuditLogsService.java) | Lists the audit trail. |
| [`GetAuditLogsByAggregateService`](../../../src/main/java/application/domain/services/GetAuditLogsByAggregateService.java) | Audit trail of one aggregate (order, product...). |
| [`GenerateAdministrativeReportService`](../../../src/main/java/application/domain/services/GenerateAdministrativeReportService.java) | Consolidated report: users by role/status, warehouses by type, products by status/type, stock totals, orders by status, total sales, invoices, shipments, returns and refunded amount. |

## 3. Business Rules Enforced

1. Reports and audit are consulted by the Administrator and the Supervisor only.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `GET` | `/api/reports/summary` | ADMIN, SUPERVISOR |
| `GET` | `/api/audit` | ADMIN, SUPERVISOR |
| `GET` | `/api/audit/aggregate/{id}` | ADMIN, SUPERVISOR |
