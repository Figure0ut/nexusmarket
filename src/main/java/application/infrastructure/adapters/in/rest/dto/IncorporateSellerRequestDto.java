package application.infrastructure.adapters.in.rest.dto;

/**
 * Incorporation of a seller by the Administrator, together with its first warehouse (section 6.1, step 1).
 */
public record IncorporateSellerRequestDto(String identifier, String identityDocument, String fullName, String email,
                                          String taxIdentifier, String corporateName,
                                          FirstWarehouse firstWarehouse) {

    public record FirstWarehouse(String warehouseId, String name, String location) {
    }
}
