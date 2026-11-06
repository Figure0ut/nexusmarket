package application.infrastructure.config;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.RegisterUserService;
import application.domain.valueobjects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bootstraps the first Administrator account.
 * <p>
 * Every operation requires an authenticated user (RG-01) and sellers, warehouses and internal users can only be
 * registered by an Administrator, so the platform needs one Administrator to exist when it starts.
 * Its data is configured in application.properties (nexusmarket.initial-admin.*).
 */
@Configuration
public class InitialAdministratorConfig {

    private static final Logger log = LoggerFactory.getLogger(InitialAdministratorConfig.class);

    @Bean
    public ApplicationRunner initialAdministratorRunner(UserRepositoryPort userRepositoryPort,
                                                        RegisterUserService registerUserService,
                                                        @Value("${nexusmarket.initial-admin.identifier}") String identifier,
                                                        @Value("${nexusmarket.initial-admin.identity-document}") String identityDocument,
                                                        @Value("${nexusmarket.initial-admin.full-name}") String fullName,
                                                        @Value("${nexusmarket.initial-admin.email}") String email) {
        return args -> {
            if (userRepositoryPort.findById(identifier).isEmpty() && !userRepositoryPort.existsByEmail(new Email(email))) {
                registerUserService.registerUser(new User(identifier, identityDocument, fullName, new Email(email),
                        UserRole.ADMIN, UserStatus.ACTIVE));
                log.info("Initial administrator created: {} ({}). Use header X-User-Email: {}", identifier, email, email);
            }
        };
    }
}
