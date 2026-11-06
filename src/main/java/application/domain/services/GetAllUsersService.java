package application.domain.services;

import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for listing all marketplace users (OBJ-01).
 */
public class GetAllUsersService {

    private final UserRepositoryPort repositoryPort;

    public GetAllUsersService(UserRepositoryPort repositoryPort) {
        if (repositoryPort == null) {
            throw new IllegalArgumentException("UserRepositoryPort cannot be null.");
        }
        this.repositoryPort = repositoryPort;
    }

    public List<User> getAllUsers() {
        return repositoryPort.findAll();
    }
}
