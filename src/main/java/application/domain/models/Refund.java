package application.domain.models;

import application.domain.enums.RefundStatus;
import application.domain.valueobjects.Money;

import java.util.Objects;

public class Refund {

    private String refundId;
    private String returnId;
    private String buyerId;
    private Money amount;
    private RefundStatus status;

    public Refund() {
        this.status = RefundStatus.PENDING;
    }

    public Refund(String refundId, String returnId, String buyerId, Money amount, RefundStatus status) {
        setRefundId(refundId);
        setReturnId(returnId);
        setBuyerId(buyerId);
        setAmount(amount);
        setStatus(status);
    }

    public Refund(String refundId, String returnId, String buyerId, Money amount) {
        this(refundId, returnId, buyerId, amount, RefundStatus.PENDING);
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        if (refundId == null || refundId.trim().isEmpty()) {
            throw new IllegalArgumentException("Refund ID cannot be null or empty.");
        }
        this.refundId = refundId.trim();
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty.");
        }
        this.returnId = returnId.trim();
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

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        if (amount == null || amount.isZero()) {
            throw new IllegalArgumentException("Refund amount must be greater than zero.");
        }
        this.amount = amount;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Refund status cannot be null.");
        }
        this.status = status;
    }

    public void process() {
        if (status != RefundStatus.PENDING) {
            throw new IllegalStateException("Only PENDING refunds can be processed.");
        }
        setStatus(RefundStatus.PROCESSED);
    }

    public void fail() {
        setStatus(RefundStatus.FAILED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Refund refund = (Refund) o;
        return Objects.equals(refundId, refund.refundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refundId);
    }
}
