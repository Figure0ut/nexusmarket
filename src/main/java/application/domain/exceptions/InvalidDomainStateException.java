package application.domain.exceptions;

public class InvalidDomainStateException extends DomainException {
    public InvalidDomainStateException(String message) {
        super(message);
    }
}
