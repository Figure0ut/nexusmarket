package application.infrastructure.adapters.in.rest.dto;

import java.util.List;

/**
 * Public self-registration of a buyer (process "Buyer registration").
 */
public record BuyerRegistrationRequestDto(String identifier, String identityDocument, String fullName, String email,
                                          String mainAddress, List<String> additionalAddresses, String commercialStatus) {
}
