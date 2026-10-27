# Billing & Invoicing Atomic Domain Services Specification

## 1. Context & Business Purpose
The Billing & Invoicing domain handles official commercial tax invoice generation, payment confirmation, and cancellation upon payment validation (OBJ-09 & Dominio 9). Each operation is implemented as an atomic domain service.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Dependencies**: [`InvoiceRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/InvoiceRepositoryPort.java), [`OrderRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java)
- **Associated Models**: [`Invoice`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Invoice.java), [`Order`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Order.java)

### Services
1. **`CreateInvoiceService`**: Creates and saves a new `Invoice` for an order in `PAID` status.
2. **`GetInvoiceService`**: Retrieves an existing invoice by its identifier.
3. **`MarkInvoicePaidService`**: Transitions an invoice to `PAID`.
4. **`CancelInvoiceService`**: Transitions an invoice to `CANCELLED`.

---

## 3. Core Business Invariants & Rules

1. **Payment Trigger**: Invoices can only be issued for orders that have been `PAID` or are in downstream fulfillment.
2. **Tax Identification Rule**: Invoices require a valid corporate/personal `TaxIdentifier` and `billingAddress`.
3. **Status Transitions**: `ISSUED` -> `PAID` or `CANCELLED`.

---

## 4. Method Signatures

```java
// CreateInvoiceService
public Invoice createInvoice(String invoiceId, String orderId, String buyerId, TaxIdentifier taxIdentifier, Address billingAddress);

// GetInvoiceService
public Invoice getInvoice(String invoiceId);

// MarkInvoicePaidService
public void markInvoicePaid(String invoiceId);

// CancelInvoiceService
public void cancelInvoice(String invoiceId);
```
