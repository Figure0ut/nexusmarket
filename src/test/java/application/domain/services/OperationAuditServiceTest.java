package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.models.AuditEntry;
import application.domain.ports.out.AuditLogRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OperationAuditServiceTest {

    private OperationAuditService auditService;
    private MockAuditRepository auditRepository;

    @BeforeEach
    void setUp() {
        auditRepository = new MockAuditRepository();
        auditService = new OperationAuditService(auditRepository);
    }

    @Test
    @DisplayName("Should successfully record audit log and query by aggregate ID")
    void shouldLogAndQueryAuditEntries() {
        auditService.logOperation("AUD-1", "ADM-100", UserRole.ADMIN, "INCORPORATE_SELLER", "SEL-50", "Admin incorporated seller");
        auditService.logOperation("AUD-2", "ADM-100", UserRole.ADMIN, "SUSPEND_PRODUCT", "P-10", "Product suspended for review");

        assertEquals(2, auditService.getAllLogs().size());
        assertEquals(1, auditService.getLogsForAggregate("SEL-50").size());
        assertEquals("INCORPORATE_SELLER", auditService.getLogsForAggregate("SEL-50").get(0).getAction());
    }

    private static class MockAuditRepository implements AuditLogRepositoryPort {
        private final List<AuditEntry> entries = new ArrayList<>();

        @Override public void save(AuditEntry entry) { entries.add(entry); }
        @Override public List<AuditEntry> findAll() { return new ArrayList<>(entries); }
        @Override public List<AuditEntry> findByTargetAggregateId(String aggregateId) {
            return entries.stream().filter(e -> e.getTargetAggregateId().equals(aggregateId)).toList();
        }
    }
}
