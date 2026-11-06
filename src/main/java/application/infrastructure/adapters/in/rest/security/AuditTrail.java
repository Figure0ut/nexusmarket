package application.infrastructure.adapters.in.rest.security;

import application.domain.models.User;
import application.domain.services.LogOperationService;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Records every successful state-changing operation in the audit log (stored in MongoDB),
 * guaranteeing the traceability required by section 1.
 */
@Component
public class AuditTrail {

    private final LogOperationService logOperationService;

    public AuditTrail(LogOperationService logOperationService) {
        this.logOperationService = logOperationService;
    }

    public void record(User actor, String action, String targetAggregateId, String details) {
        logOperationService.logOperation(UUID.randomUUID().toString(), actor.getIdentifier(), actor.getRole(),
                action, targetAggregateId, details);
    }
}
