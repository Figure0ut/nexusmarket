package application.infrastructure.adapters.out.sql.entity;

import application.domain.enums.ReturnReason;
import application.domain.enums.ReturnStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "return_requests")
public class ReturnRequestEntity {

    @Id
    @Column(name = "return_id", nullable = false, unique = true)
    private String returnId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReturnReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReturnStatus status;

    public ReturnRequestEntity() {}

    public String getReturnId() { return returnId; }
    public void setReturnId(String returnId) { this.returnId = returnId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public ReturnReason getReason() { return reason; }
    public void setReason(ReturnReason reason) { this.reason = reason; }

    public ReturnStatus getStatus() { return status; }
    public void setStatus(ReturnStatus status) { this.status = status; }
}
