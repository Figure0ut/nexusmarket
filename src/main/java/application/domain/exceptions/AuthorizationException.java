package application.domain.exceptions;

/**
 * Raised when an authenticated user tries to execute an operation outside its role (RG-03)
 * or to manage information that does not belong to it.
 */
public class AuthorizationException extends DomainException {

    public AuthorizationException(String message) {
        super(message);
    }
}
