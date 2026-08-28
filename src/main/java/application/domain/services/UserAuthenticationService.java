package application.domain.services;

import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Email;

public class UserAuthenticationService {

    private final UserRepositoryPort userRepositoryPort;

    public UserAuthenticationService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public boolean authenticate(Email email) {
        return userRepositoryPort.findByEmail(email).isPresent();
    }
}
