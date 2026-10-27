package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;

public class UserRequestDto {
    private String identifier;
    private String fullName;
    private String email;
    private UserRole role;
    private UserStatus status;

    // Buyer-specific fields
    private String mainAddress;
    private String commercialStatus;

    // Seller-specific fields
    private String taxIdentifier;
    private String corporateName;

    public UserRequestDto() {}

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
