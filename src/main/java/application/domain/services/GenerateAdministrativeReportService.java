package application.domain.services;

import application.domain.enums.OrderStatus;
import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;
import application.domain.enums.RefundStatus;
import application.domain.enums.ReturnStatus;
import application.domain.enums.ShipmentStatus;
import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.enums.WarehouseType;
import application.domain.models.AdministrativeReport;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Atomic domain service responsible exclusively for consolidating administrative information
 * for consultation (OBJ-12). Used by the Administrator and the Supervisor.
 */
public class GenerateAdministrativeReportService {

    private final UserRepositoryPort userRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final ReturnRepositoryPort returnRepositoryPort;

    public GenerateAdministrativeReportService(UserRepositoryPort userRepositoryPort,
                                               WarehouseRepositoryPort warehouseRepositoryPort,
                                               ProductRepositoryPort productRepositoryPort,
                                               InventoryRepositoryPort inventoryRepositoryPort,
                                               OrderRepositoryPort orderRepositoryPort,
                                               InvoiceRepositoryPort invoiceRepositoryPort,
                                               ShipmentRepositoryPort shipmentRepositoryPort,
                                               ReturnRepositoryPort returnRepositoryPort) {
        if (userRepositoryPort == null || warehouseRepositoryPort == null || productRepositoryPort == null
                || inventoryRepositoryPort == null || orderRepositoryPort == null || invoiceRepositoryPort == null
                || shipmentRepositoryPort == null || returnRepositoryPort == null) {
            throw new IllegalArgumentException("Repository ports cannot be null.");
        }
        this.userRepositoryPort = userRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.returnRepositoryPort = returnRepositoryPort;
    }

    public AdministrativeReport generateReport() {
        var inventories = inventoryRepositoryPort.findAll();
        var orders = orderRepositoryPort.findAllOrders();

        BigDecimal totalSales = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.PENDING_PAYMENT)
                .map(Order::getTotalAmount)
                .map(m -> m.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRefunded = returnRepositoryPort.findAllRefunds().stream()
                .filter(r -> r.getStatus() == RefundStatus.PROCESSED)
                .map(Refund::getAmount)
                .map(m -> m.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdministrativeReport(
                Instant.now(),
                count(userRepositoryPort.findAll(), u -> u.getRole().name(), UserRole.values()),
                count(userRepositoryPort.findAll(), u -> u.getStatus().name(), UserStatus.values()),
                count(warehouseRepositoryPort.findAll(), w -> w.getType().name(), WarehouseType.values()),
                count(productRepositoryPort.findAll(), p -> p.getStatus().name(), ProductStatus.values()),
                count(productRepositoryPort.findAll(), p -> p.getProductType().name(), ProductType.values()),
                inventories.stream().mapToLong(i -> i.getAvailableStock().getValue()).sum(),
                inventories.stream().mapToLong(i -> i.getReservedStock().getValue()).sum(),
                inventories.stream().mapToLong(Inventory::getDamagedStockValue).sum(),
                count(orders, o -> o.getStatus().name(), OrderStatus.values()),
                totalSales,
                invoiceRepositoryPort.findAll().size(),
                count(shipmentRepositoryPort.findAll(), s -> s.getStatus().name(), ShipmentStatus.values()),
                count(returnRepositoryPort.findAllReturns(), r -> r.getStatus().name(), ReturnStatus.values()),
                totalRefunded
        );
    }

    private static <T> Map<String, Long> count(Collection<T> items, Function<T, String> key, Enum<?>[] allValues) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Enum<?> value : allValues) {
            result.put(value.name(), 0L);
        }
        for (T item : items) {
            result.merge(key.apply(item), 1L, Long::sum);
        }
        return result;
    }
}
