package application.infrastructure.adapters.in.rest.dto;

import application.domain.models.AdministrativeReport;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record AdministrativeReportResponseDto(
        Instant generatedAt,
        Map<String, Long> usersByRole,
        Map<String, Long> usersByStatus,
        Map<String, Long> warehousesByType,
        Map<String, Long> productsByStatus,
        Map<String, Long> productsByType,
        long totalAvailableStock,
        long totalReservedStock,
        long totalDamagedStock,
        Map<String, Long> ordersByStatus,
        BigDecimal totalSalesAmount,
        long invoicesIssued,
        Map<String, Long> shipmentsByStatus,
        Map<String, Long> returnsByStatus,
        BigDecimal totalRefundedAmount) {

    public static AdministrativeReportResponseDto fromDomain(AdministrativeReport r) {
        return new AdministrativeReportResponseDto(r.getGeneratedAt(), r.getUsersByRole(), r.getUsersByStatus(),
                r.getWarehousesByType(), r.getProductsByStatus(), r.getProductsByType(),
                r.getTotalAvailableStock(), r.getTotalReservedStock(), r.getTotalDamagedStock(),
                r.getOrdersByStatus(), r.getTotalSalesAmount(), r.getInvoicesIssued(),
                r.getShipmentsByStatus(), r.getReturnsByStatus(), r.getTotalRefundedAmount());
    }
}
