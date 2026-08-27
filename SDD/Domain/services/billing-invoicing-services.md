# Billing & Invoicing Domain Service Specification

## 1. Context & Business Purpose
The **Billing & Invoicing Domain Service** handles official commercial tax invoice generation upon payment validation (OBJ-09 & Dominio 9).

---

## 2. Domain Organizations & Package Location
- **Package**: `com.nexusmarket.domain.service.billing`
- **Associated Aggregate Roots**: [`Invoice`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/billing/Invoice.java), [`Order`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/order/Order.java)
- **Associated Value Objects**: [`TaxIdentifier`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/TaxIdentifier.java), [`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Address.java), [`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Money.java), [`InvoiceStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/billing/InvoiceStatus.java)

---

## 3. Core Business Invariants & Rules

1. **Payment Trigger**: Invoices are issued when an order transitions to `PAID`.
2. **Tax Identification Rule**: Invoices require a valid corporate/personal `TaxIdentifier` and `billingAddress`.
3. **Status Lifecycle**: Initial state is `ISSUED`, transitioning to `PAID` or `CANCELLED`.
