package application.infrastructure.adapters.out.sql.entity;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "identifier", nullable = false, unique = true)
    private String identifier;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    // Buyer-specific
    @Column(name = "main_address")
    private String mainAddress;

    @Column(name = "commercial_status")
    private String commercialStatus;

    // Seller-specific
    @Column(name = "tax_identifier")
    private String taxIdentifier;

    @Column(name = "corporate_name")
    private String corporateName;

    public UserEntity() {}

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getMainAddress() { return mainAddress; }
    public void setMainAddress(String mainAddress) { this.mainAddress = mainAddress; }

    public String getCommercialStatus() { return commercialStatus; }
    public void setCommercialStatus(String commercialStatus) { this.commercialStatus = commercialStatus; }

    public String getTaxIdentifier() { return taxIdentifier; }
    public void setTaxIdentifier(String taxIdentifier) { this.taxIdentifier = taxIdentifier; }

    public String getCorporateName() { return corporateName; }
    public void setCorporateName(String corporateName) { this.corporateName = corporateName; }
}
