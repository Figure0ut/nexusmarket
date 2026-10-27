package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.User;

public class UserResponseDto {
    private String identifier;
    private String fullName;
    private String email;
    private UserRole role;
    private UserStatus status;
    private String mainAddress;
    private String commercialStatus;
    private String taxIdentifier;
    private String corporateName;

    public UserResponseDto() {}

    public static UserResponseDto fromDomain(User user) {
        if (user == null) return null;
        UserResponseDto dto = new UserResponseDto();
        dto.setIdentifier(user.getIdentifier());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail() != null ? user.getEmail().getValue() : null);
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());

        if (user instanceof Buyer buyer) {
            dto.setMainAddress(buyer.getMainAddress() != null ? buyer.getMainAddress().getStreet() : null);
            dto.setCommercialStatus(buyer.getCommercialStatus());
        } else if (user instanceof Seller seller) {
            dto.setTaxIdentifier(seller.getTaxIdentifier() != null ? seller.getTaxIdentifier().getTaxId() : null);
            dto.setCorporateName(seller.getCorporateName());
        }
        return dto;
    }

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
