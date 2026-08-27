package com.nexusmarket.domain.model.billing;

import com.nexusmarket.domain.model.common.Address;
import com.nexusmarket.domain.model.common.Money;
import com.nexusmarket.domain.model.common.TaxIdentifier;

import java.util.Objects;

/**
 * Pure Java Domain Aggregate Root representing a commercial Invoice in NexusMarket.
 */
public class Invoice {

    private final String invoiceId;
    private final String orderId;
    private final String buyerId;
    private final TaxIdentifier taxIdentifier;
    private final Address billingAddress;
    private final Money totalAmount;
    private InvoiceStatus status;

    public Invoice(String invoiceId, String orderId, String buyerId, TaxIdentifier taxIdentifier,
                   Address billingAddress, Money totalAmount, InvoiceStatus status) {
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invoice ID cannot be null or empty.");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        if (taxIdentifier == null) {
            throw new IllegalArgumentException("Tax Identifier cannot be null.");
        }
        if (billingAddress == null) {
            throw new IllegalArgumentException("Billing Address cannot be null.");
        }
        if (totalAmount == null) {
            throw new IllegalArgumentException("Invoice total amount cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Invoice status cannot be null.");
        }

        this.invoiceId = invoiceId.trim();
        this.orderId = orderId.trim();
        this.buyerId = buyerId.trim();
        this.taxIdentifier = taxIdentifier;
        this.billingAddress = billingAddress;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Invoice(String invoiceId, String orderId, String buyerId, String taxIdStr, Address billingAddress, Money totalAmount) {
        this(invoiceId, orderId, buyerId, new TaxIdentifier(taxIdStr), billingAddress, totalAmount, InvoiceStatus.ISSUED);
    }

    public void markAsPaid() {
        this.status = InvoiceStatus.PAID;
    }

    public void cancel() {
        this.status = InvoiceStatus.CANCELLED;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public TaxIdentifier getTaxIdentifier() {
        return taxIdentifier;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
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
