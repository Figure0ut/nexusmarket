package application.infrastructure.adapters.in.rest.dto;

public class IncorporateSellerRequestDto {
    private String adminIdentifier;

    public IncorporateSellerRequestDto() {}

    public String getAdminIdentifier() { return adminIdentifier; }
    public void setAdminIdentifier(String adminIdentifier) { this.adminIdentifier = adminIdentifier; }
}
