package com.nexusmarket.domain.model.user;

import com.nexusmarket.domain.model.common.Email;
import com.nexusmarket.domain.model.common.TaxIdentifier;

/**
 * Pure Java Domain Entity representing a Seller in NexusMarket.
 */
public class Seller extends User {

    private final TaxIdentifier taxIdentifier;
    private final String corporateName;

    public Seller(String identifier, String fullName, Email email, UserRole role, UserStatus status,
                  TaxIdentifier taxIdentifier, String corporateName) {
        super(identifier, fullName, email, role, status);
        this.taxIdentifier = taxIdentifier;
        this.corporateName = corporateName != null ? corporateName.trim() : fullName;
    }

    public Seller(String identifier, String fullName, String emailStr, String taxIdStr, String corporateName) {
        this(identifier, fullName, new Email(emailStr), UserRole.SELLER, UserStatus.PENDING_INCORPORATION,
                new TaxIdentifier(taxIdStr), corporateName);
    }

    public Seller(String identifier, String fullName, String emailStr) {
        this(identifier, fullName, emailStr, "TAX-" + identifier, fullName);
    }

    public void incorporate(User adminUser) {
        if (adminUser == null) {
            throw new IllegalArgumentException("Admin user authorizing incorporation cannot be null.");
        }
        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Incorporation Failure: User '" + adminUser.getIdentifier() +
                    "' with role '" + adminUser.getRole() + "' is not authorized to incorporate sellers. ADMIN role required.");
        }
        if (getStatus() == UserStatus.ACTIVE) {
            throw new IllegalStateException("Seller '" + getIdentifier() + "' is already incorporated and active.");
        }

        changeStatus(UserStatus.ACTIVE);
    }

    public TaxIdentifier getTaxIdentifier() {
        return taxIdentifier;
    }

    public String getCorporateName() {
        return corporateName;
    }
}
