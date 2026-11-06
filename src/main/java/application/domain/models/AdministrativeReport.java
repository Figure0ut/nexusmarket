package application.domain.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * Consolidated administrative information for consultation (OBJ-12 / "Consulta de reportes administrativos").
 * Read-only snapshot generated on demand.
 */
public class AdministrativeReport {

    private final Instant generatedAt;
    private final Map<String, Long> usersByRole;
    private final Map<String, Long> usersByStatus;
    private final Map<String, Long> warehousesByType;
    private final Map<String, Long> productsByStatus;
    private final Map<String, Long> productsByType;
    private final long totalAvailableStock;
    private final long totalReservedStock;
    private final long totalDamagedStock;
    private final Map<String, Long> ordersByStatus;
    private final BigDecimal totalSalesAmount;
    private final long invoicesIssued;
    private final Map<String, Long> shipmentsByStatus;
    private final Map<String, Long> returnsByStatus;
    private final BigDecimal totalRefundedAmount;

    public AdministrativeReport(Instant generatedAt,
                                Map<String, Long> usersByRole, Map<String, Long> usersByStatus,
                                Map<String, Long> warehousesByType,
                                Map<String, Long> productsByStatus, Map<String, Long> productsByType,
                                long totalAvailableStock, long totalReservedStock, long totalDamagedStock,
                                Map<String, Long> ordersByStatus, BigDecimal totalSalesAmount, long invoicesIssued,
                                Map<String, Long> shipmentsByStatus, Map<String, Long> returnsByStatus,
                                BigDecimal totalRefundedAmount) {
        this.generatedAt = generatedAt;
        this.usersByRole = Collections.unmodifiableMap(usersByRole);
        this.usersByStatus = Collections.unmodifiableMap(usersByStatus);
        this.warehousesByType = Collections.unmodifiableMap(warehousesByType);
        this.productsByStatus = Collections.unmodifiableMap(productsByStatus);
        this.productsByType = Collections.unmodifiableMap(productsByType);
        this.totalAvailableStock = totalAvailableStock;
        this.totalReservedStock = totalReservedStock;
        this.totalDamagedStock = totalDamagedStock;
        this.ordersByStatus = Collections.unmodifiableMap(ordersByStatus);
        this.totalSalesAmount = totalSalesAmount;
        this.invoicesIssued = invoicesIssued;
        this.shipmentsByStatus = Collections.unmodifiableMap(shipmentsByStatus);
        this.returnsByStatus = Collections.unmodifiableMap(returnsByStatus);
        this.totalRefundedAmount = totalRefundedAmount;
    }

    public Instant getGeneratedAt() { return generatedAt; }
    public Map<String, Long> getUsersByRole() { return usersByRole; }
    public Map<String, Long> getUsersByStatus() { return usersByStatus; }
    public Map<String, Long> getWarehousesByType() { return warehousesByType; }
    public Map<String, Long> getProductsByStatus() { return productsByStatus; }
    public Map<String, Long> getProductsByType() { return productsByType; }
    public long getTotalAvailableStock() { return totalAvailableStock; }
    public long getTotalReservedStock() { return totalReservedStock; }
    public long getTotalDamagedStock() { return totalDamagedStock; }
    public Map<String, Long> getOrdersByStatus() { return ordersByStatus; }
    public BigDecimal getTotalSalesAmount() { return totalSalesAmount; }
    public long getInvoicesIssued() { return invoicesIssued; }
    public Map<String, Long> getShipmentsByStatus() { return shipmentsByStatus; }
    public Map<String, Long> getReturnsByStatus() { return returnsByStatus; }
    public BigDecimal getTotalRefundedAmount() { return totalRefundedAmount; }
}
