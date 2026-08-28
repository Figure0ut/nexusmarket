package application.domain.models;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.TaxIdentifier;

public class Seller extends User {

    private TaxIdentifier taxIdentifier;
    private String corporateName;

    public Seller() {
        super();
        setRole(UserRole.SELLER);
        setStatus(UserStatus.PENDING_INCORPORATION);
    }

    public Seller(String identifier, String fullName, Email email, UserRole role, UserStatus status,
                  TaxIdentifier taxIdentifier, String corporateName) {
        super(identifier, fullName, email, role, status);
        setTaxIdentifier(taxIdentifier);
        setCorporateName(corporateName);
    }

    public Seller(String identifier, String fullName, String emailStr, String taxIdStr, String corporateName) {
        this(identifier, fullName, new Email(emailStr), UserRole.SELLER, UserStatus.PENDING_INCORPORATION,
                new TaxIdentifier(taxIdStr), corporateName);
    }

    public Seller(String identifier, String fullName, String emailStr) {
        this(identifier, fullName, emailStr, "TAX-" + identifier, fullName);
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
        this.corporateName = corporateName != null ? corporateName.trim() : getFullName();
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
}
