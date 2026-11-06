package application.infrastructure.adapters.in.rest.security;

/**
 * RG-01: every operation must be executed by an authenticated user.
 * Raised when the request does not identify an existing ACTIVE user.
 */
public class AuthenticationRequiredException extends RuntimeException {

    public AuthenticationRequiredException(String message) {
        super(message);
    }
}
