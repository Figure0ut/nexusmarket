package com.nexusmarket.domain.model.returns;

import com.nexusmarket.domain.model.common.Money;

import java.util.Objects;

/**
 * Pure Java Domain Aggregate Root representing a financial Refund reimbursement.
 */
public class Refund {

    private final String refundId;
    private final String returnId;
    private final String buyerId;
    private final Money amount;
    private RefundStatus status;

    public Refund(String refundId, String returnId, String buyerId, Money amount, RefundStatus status) {
        if (refundId == null || refundId.trim().isEmpty()) {
            throw new IllegalArgumentException("Refund ID cannot be null or empty.");
        }
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty.");
        }
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        if (amount == null || amount.isZero()) {
            throw new IllegalArgumentException("Refund amount must be greater than zero.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Refund status cannot be null.");
        }

        this.refundId = refundId.trim();
        this.returnId = returnId.trim();
        this.buyerId = buyerId.trim();
        this.amount = amount;
        this.status = status;
    }

    public Refund(String refundId, String returnId, String buyerId, Money amount) {
        this(refundId, returnId, buyerId, amount, RefundStatus.PENDING);
    }

    public void process() {
        if (status != RefundStatus.PENDING) {
            throw new IllegalStateException("Only PENDING refunds can be processed.");
        }
        this.status = RefundStatus.PROCESSED;
    }

    public void fail() {
        this.status = RefundStatus.FAILED;
    }

    public String getRefundId() {
        return refundId;
    }

    public String getReturnId() {
        return returnId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public Money getAmount() {
        return amount;
    }

    public RefundStatus getStatus() {
        return status;
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
