package application.infrastructure.adapters.in.rest.dto;

/**
 * Authentication request. The email is the main access channel of a user (Domain 1).
 */
public record LoginRequestDto(String email) {
}
