package application.infrastructure.adapters.in.rest.security;

import application.domain.exceptions.DomainException;
import application.domain.models.User;
import application.domain.services.AuthenticateUserService;
import application.domain.valueobjects.Email;
import org.springframework.stereotype.Component;

/**
 * Inbound adapter helper that resolves the authenticated user of every HTTP request (RG-01).
 * <p>
 * The specification states that the email is the main access channel of a user (Domain 1) and
 * explicitly leaves technical authentication mechanisms out of scope (section 3.2). Therefore each
 * request identifies its user with the {@value #USER_EMAIL_HEADER} header, and the domain
 * {@link AuthenticateUserService} checks that the user exists and is ACTIVE.
 */
@Component
public class AuthenticatedUserResolver {

    public static final String USER_EMAIL_HEADER = "X-User-Email";

    private final AuthenticateUserService authenticateUserService;

    public AuthenticatedUserResolver(AuthenticateUserService authenticateUserService) {
        this.authenticateUserService = authenticateUserService;
    }

    public User resolve(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new AuthenticationRequiredException("RG-01: every operation must be executed by an authenticated user. "
                    + "Send the '" + USER_EMAIL_HEADER + "' header with the email of an ACTIVE user.");
        }
        try {
            return authenticateUserService.authenticate(new Email(email));
        } catch (DomainException | IllegalArgumentException ex) {
            throw new AuthenticationRequiredException("Authentication failure: " + ex.getMessage());
        }
    }
}
