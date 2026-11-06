package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.exceptions.AuthorizationException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Atomic domain service responsible exclusively for validating user authorization
 * (RG-01, RG-02, RG-03 and the Responsibility Matrix of section 12).
 */
public class ValidateAuthorizationService {

    /**
     * Business processes of section 3.1 grouped by responsibility.
     */
    public enum BusinessProcess {
        // ---- Processes explicitly listed in the Responsibility Matrix (section 12) ----
        /** Seller registration: Admin. */
        SELLER_REGISTRATION,
        /** Product registration: Seller. */
        PRODUCT_REGISTRATION,
        /** Inventory administration: Seller, Logistics Operator. */
        INVENTORY_MANAGEMENT,
        /** Order management: Buyer, Seller, Logistics Operator. */
        ORDER_MANAGEMENT,
        /** Refund management: Buyer, Admin. */
        REFUND_MANAGEMENT,

        // ---- Processes not listed in the matrix, assigned from the participant descriptions (section 5) ----
        /** Administrator: "responsible for the administration of sellers and warehouses". */
        WAREHOUSE_MANAGEMENT,
        /** Administration of internal user accounts: only the Administrator has an administrative profile. */
        USER_ADMINISTRATION,
        /** Buyer: "person who acquires products" / "the buyer selects products through the cart". */
        CART_MANAGEMENT,
        /** Logistics Operator: "in charge of the physical operation of warehouses and dispatches". */
        SHIPMENT_MANAGEMENT,
        /** Administrative reports consultation: Administrator and Supervisor ("consultation and operational follow-up profile"). */
        ADMINISTRATIVE_REPORTS
    }

    private static final Map<BusinessProcess, Set<UserRole>> RESPONSIBILITY_MATRIX = Map.of(
            BusinessProcess.SELLER_REGISTRATION, EnumSet.of(UserRole.ADMIN),
            BusinessProcess.PRODUCT_REGISTRATION, EnumSet.of(UserRole.SELLER),
            BusinessProcess.INVENTORY_MANAGEMENT, EnumSet.of(UserRole.SELLER, UserRole.OPERATOR_LOGISTIC),
            BusinessProcess.ORDER_MANAGEMENT, EnumSet.of(UserRole.BUYER, UserRole.SELLER, UserRole.OPERATOR_LOGISTIC),
            BusinessProcess.REFUND_MANAGEMENT, EnumSet.of(UserRole.BUYER, UserRole.ADMIN),
            BusinessProcess.WAREHOUSE_MANAGEMENT, EnumSet.of(UserRole.ADMIN),
            BusinessProcess.USER_ADMINISTRATION, EnumSet.of(UserRole.ADMIN),
            BusinessProcess.CART_MANAGEMENT, EnumSet.of(UserRole.BUYER),
            BusinessProcess.SHIPMENT_MANAGEMENT, EnumSet.of(UserRole.OPERATOR_LOGISTIC),
            BusinessProcess.ADMINISTRATIVE_REPORTS, EnumSet.of(UserRole.ADMIN, UserRole.SUPERVISOR)
    );

    /**
     * Validates that the actor can EXECUTE (create/modify) operations of the given process.
     */
    public void validateProcessAuthorization(User actor, BusinessProcess process) {
        validateActor(actor);
        if (process == null) {
            throw new IllegalArgumentException("Business process cannot be null.");
        }
        if (!RESPONSIBILITY_MATRIX.get(process).contains(actor.getRole())) {
            throw new AuthorizationException("Authorization Failure (RG-03): User '" + actor.getIdentifier() +
                    "' with role '" + actor.getRole() + "' is not authorized for process '" + process + "'.");
        }
    }

    /**
     * Validates that the actor can CONSULT information of the given process.
     * Besides the roles responsible for the process, the Supervisor can consult it,
     * because the Supervisor is the "consultation and operational follow-up profile" (section 5).
     */
    public void validateQueryAuthorization(User actor, BusinessProcess process) {
        validateActor(actor);
        if (process == null) {
            throw new IllegalArgumentException("Business process cannot be null.");
        }
        if (actor.getRole() == UserRole.SUPERVISOR) {
            return;
        }
        validateProcessAuthorization(actor, process);
    }

    /**
     * RG-03 ownership rule: Buyers and Sellers can only manage their own information.
     * (e.g. a buyer never manages information of other buyers; a seller only manages its own products).
     */
    public void validateOwnership(User actor, String ownerId) {
        validateActor(actor);
        if ((actor.getRole() == UserRole.BUYER || actor.getRole() == UserRole.SELLER)
                && (ownerId == null || !actor.getIdentifier().equals(ownerId))) {
            throw new AuthorizationException("Authorization Failure (RG-03): User '" + actor.getIdentifier() +
                    "' cannot manage information that belongs to '" + ownerId + "'.");
        }
    }

    public boolean isAuthorized(User actor, BusinessProcess process) {
        if (actor == null || !actor.isActive() || process == null) {
            return false;
        }
        return RESPONSIBILITY_MATRIX.get(process).contains(actor.getRole());
    }

    private void validateActor(User actor) {
        if (actor == null) {
            throw new IllegalArgumentException("Actor cannot be null (RG-01: every operation requires an authenticated user).");
        }
        if (!actor.isActive()) {
            throw new InvalidDomainStateException("Authorization Failure: Actor '" + actor.getIdentifier() +
                    "' is not ACTIVE (Current status: " + actor.getStatus() + ").");
        }
    }
}
