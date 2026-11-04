package application.infrastructure.adapters.out.sql.entity;

import application.domain.enums.InvoiceStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoices")
public class InvoiceEntity {

    @Id
    @Column(name = "invoice_id", nullable = false, unique = true)
    private String invoiceId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    @Column(name = "tax_identifier", nullable = false)
    private String taxIdentifier;

    @Column(name = "billing_address", nullable = false)
    private String billingAddress;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    public InvoiceEntity() {}

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public String getTaxIdentifier() { return taxIdentifier; }
    public void setTaxIdentifier(String taxIdentifier) { this.taxIdentifier = taxIdentifier; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
}
