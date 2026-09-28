package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class AuthorizationService {

    public enum BusinessProcess {
        SELLER_REGISTRATION,
        PRODUCT_REGISTRATION,
        INVENTORY_MANAGEMENT,
        ORDER_MANAGEMENT,
        REFUND_MANAGEMENT
    }

    private static final Map<BusinessProcess, Set<UserRole>> RESPONSIBILITY_MATRIX = Map.of(
            BusinessProcess.SELLER_REGISTRATION, EnumSet.of(UserRole.ADMIN),
            BusinessProcess.PRODUCT_REGISTRATION, EnumSet.of(UserRole.SELLER),
            BusinessProcess.INVENTORY_MANAGEMENT, EnumSet.of(UserRole.SELLER, UserRole.OPERATOR_LOGISTIC),
            BusinessProcess.ORDER_MANAGEMENT, EnumSet.of(UserRole.BUYER, UserRole.SELLER, UserRole.OPERATOR_LOGISTIC),
            BusinessProcess.REFUND_MANAGEMENT, EnumSet.of(UserRole.BUYER, UserRole.ADMIN)
    );

    public void validateProcessAuthorization(User actor, BusinessProcess process) {
        if (actor == null) {
            throw new IllegalArgumentException("Actor cannot be null (RG-01: Authentication required).");
        }
        if (actor.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidDomainStateException("Authorization Failure: Actor '" + actor.getIdentifier() +
                    "' is not ACTIVE (Current status: " + actor.getStatus() + ").");
        }
        if (process == null) {
            throw new IllegalArgumentException("Business process cannot be null.");
        }

        Set<UserRole> allowedRoles = RESPONSIBILITY_MATRIX.get(process);
        if (allowedRoles == null || !allowedRoles.contains(actor.getRole())) {
            throw new InvalidDomainStateException("Authorization Failure (RG-03): User '" + actor.getIdentifier() +
                    "' with role '" + actor.getRole() + "' is not authorized for process '" + process + "'.");
        }
    }

    public boolean isAuthorized(User actor, BusinessProcess process) {
        if (actor == null || actor.getStatus() != UserStatus.ACTIVE || process == null) {
            return false;
        }
        Set<UserRole> allowedRoles = RESPONSIBILITY_MATRIX.get(process);
        return allowedRoles != null && allowedRoles.contains(actor.getRole());
    }
}
