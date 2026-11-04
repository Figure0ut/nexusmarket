package application.infrastructure.adapters.out.sql.entity;

import application.domain.enums.RefundStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "refunds")
public class RefundEntity {

    @Id
    @Column(name = "refund_id", nullable = false, unique = true)
    private String refundId;

    @Column(name = "return_id", nullable = false)
    private String returnId;

    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RefundStatus status;

    public RefundEntity() {}

    public String getRefundId() { return refundId; }
    public void setRefundId(String refundId) { this.refundId = refundId; }

    public String getReturnId() { return returnId; }
    public void setReturnId(String returnId) { this.returnId = returnId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }
}
