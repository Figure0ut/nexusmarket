package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.InvoiceStatus;
import application.domain.models.Invoice;

public class InvoiceResponseDto {
    private String invoiceId;
    private String orderId;
    private String buyerId;
    private String taxIdentifier;
    private String billingAddress;
    private double totalAmount;
    private InvoiceStatus status;

    public InvoiceResponseDto() {}

    public static InvoiceResponseDto fromDomain(Invoice invoice) {
        if (invoice == null) return null;
        InvoiceResponseDto dto = new InvoiceResponseDto();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setOrderId(invoice.getOrderId());
        dto.setBuyerId(invoice.getBuyerId());
        dto.setTaxIdentifier(invoice.getTaxIdentifier() != null ? invoice.getTaxIdentifier().getTaxId() : null);
        dto.setBillingAddress(invoice.getBillingAddress() != null ? invoice.getBillingAddress().getStreet() : null);
        dto.setTotalAmount(invoice.getTotalAmount() != null ? invoice.getTotalAmount().getAmount().doubleValue() : 0.0);
        dto.setStatus(invoice.getStatus());
        return dto;
    }

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

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
}
