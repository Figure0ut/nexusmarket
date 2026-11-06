package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.UserRole;

/**
 * Registration of an internal user (ADMIN, OPERATOR_LOGISTIC or SUPERVISOR) by the Administrator.
 */
public record InternalUserRequestDto(String identifier, String identityDocument, String fullName, String email, UserRole role) {
}
