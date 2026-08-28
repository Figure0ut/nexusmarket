package application.domain.models;

import application.domain.enums.InvoiceStatus;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.TaxIdentifier;

import java.util.Objects;

public class Invoice {

    private String invoiceId;
    private String orderId;
    private String buyerId;
    private TaxIdentifier taxIdentifier;
    private Address billingAddress;
    private Money totalAmount;
    private InvoiceStatus status;

    public Invoice() {
        this.status = InvoiceStatus.ISSUED;
    }

    public Invoice(String invoiceId, String orderId, String buyerId, TaxIdentifier taxIdentifier,
                   Address billingAddress, Money totalAmount, InvoiceStatus status) {
        setInvoiceId(invoiceId);
        setOrderId(orderId);
        setBuyerId(buyerId);
        setTaxIdentifier(taxIdentifier);
        setBillingAddress(billingAddress);
        setTotalAmount(totalAmount);
        setStatus(status);
    }

    public Invoice(String invoiceId, String orderId, String buyerId, String taxIdStr, Address billingAddress, Money totalAmount) {
        this(invoiceId, orderId, buyerId, new TaxIdentifier(taxIdStr), billingAddress, totalAmount, InvoiceStatus.ISSUED);
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invoice ID cannot be null or empty.");
        }
        this.invoiceId = invoiceId.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        this.orderId = orderId.trim();
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        this.buyerId = buyerId.trim();
    }

    public TaxIdentifier getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setTaxIdentifier(TaxIdentifier taxIdentifier) {
        if (taxIdentifier == null) {
            throw new IllegalArgumentException("Tax Identifier cannot be null.");
        }
        this.taxIdentifier = taxIdentifier;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        if (billingAddress == null) {
            throw new IllegalArgumentException("Billing Address cannot be null.");
        }
        this.billingAddress = billingAddress;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Money totalAmount) {
        if (totalAmount == null) {
            throw new IllegalArgumentException("Invoice total amount cannot be null.");
        }
        this.totalAmount = totalAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Invoice status cannot be null.");
        }
        this.status = status;
    }

    public void markAsPaid() {
        setStatus(InvoiceStatus.PAID);
    }

    public void cancel() {
        setStatus(InvoiceStatus.CANCELLED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(invoiceId, invoice.invoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId);
    }
}
