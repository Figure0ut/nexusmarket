# Billing & Invoicing Domain Service Specification

## 1. Context & Business Purpose
The **`BillingInvoicingService`** handles official commercial tax invoice generation upon payment validation (OBJ-09 & Dominio 9).

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`BillingInvoicingService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/BillingInvoicingService.java)
- **Dependencies**: [`InvoiceRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/InvoiceRepositoryPort.java), [`OrderRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java)
- **Associated Models**: [`Invoice`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Invoice.java), [`Order`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Order.java)

---

## 3. Core Business Invariants & Rules

1. **Payment Trigger**: Invoices can only be issued for orders that have been `PAID` or are in downstream fulfillment.
2. **Tax Identification Rule**: Invoices require a valid corporate/personal `TaxIdentifier` and `billingAddress`.
3. **Status Transitions**: `ISSUED` -> `PAID` or `CANCELLED`.

---

## 4. Method Signatures

```java
public Invoice generateInvoice(String invoiceId, String orderId, String buyerId, TaxIdentifier taxIdentifier, Address billingAddress);
public Invoice getInvoice(String invoiceId);
public void markInvoicePaid(String invoiceId);
public void cancelInvoice(String invoiceId);
```
