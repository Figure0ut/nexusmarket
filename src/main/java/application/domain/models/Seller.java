package application.domain.models;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.TaxIdentifier;

/**
 * Seller (Domain 3 - Seller Management).
 * <p>
 * Business rule: sellers cannot self-register; they are incorporated by the Administrator
 * (section 6.1, step 1), who registers the seller together with its first warehouse.
 */
public class Seller extends User {

    private TaxIdentifier taxIdentifier;
    private String corporateName;

    public Seller() {
        super();
        setRole(UserRole.SELLER);
        setStatus(UserStatus.PENDING_INCORPORATION);
    }

    public Seller(String identifier, String identityDocument, String fullName, Email email, UserStatus status,
                  TaxIdentifier taxIdentifier, String corporateName) {
        super(identifier, identityDocument, fullName, email, UserRole.SELLER, status);
        setTaxIdentifier(taxIdentifier);
        setCorporateName(corporateName);
    }

    public Seller(String identifier, String identityDocument, String fullName, String emailStr,
                  String taxIdStr, String corporateName) {
        this(identifier, identityDocument, fullName, new Email(emailStr), UserStatus.PENDING_INCORPORATION,
                taxIdStr != null ? new TaxIdentifier(taxIdStr) : null, corporateName);
    }

    @Override
    public void setRole(UserRole role) {
        if (role != UserRole.SELLER) {
            throw new IllegalArgumentException("A Seller must always have the SELLER role (RG-02).");
        }
        super.setRole(role);
    }

    public TaxIdentifier getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setTaxIdentifier(TaxIdentifier taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = (corporateName != null && !corporateName.trim().isEmpty()) ? corporateName.trim() : getFullName();
    }

    /**
     * Incorporates the seller into the marketplace. Only an ACTIVE Administrator can do it.
     */
    public void incorporate(User adminUser) {
        if (adminUser == null) {
            throw new IllegalArgumentException("Admin user authorizing incorporation cannot be null.");
        }
        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Incorporation Failure: User '" + adminUser.getIdentifier() +
                    "' with role '" + adminUser.getRole() + "' is not authorized to incorporate sellers. ADMIN role required.");
        }
        if (!adminUser.isActive()) {
            throw new IllegalStateException("Incorporation Failure: Admin '" + adminUser.getIdentifier() + "' is not ACTIVE.");
        }
        if (getStatus() == UserStatus.ACTIVE) {
            throw new IllegalStateException("Seller '" + getIdentifier() + "' is already incorporated and active.");
        }

        changeStatus(UserStatus.ACTIVE);
    }
}
