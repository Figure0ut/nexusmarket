package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Address;

/**
 * Atomic domain service responsible exclusively for updating the main delivery address of a buyer (Domain 2).
 */
public class UpdateBuyerMainAddressService {

    private final UserRepositoryPort repositoryPort;

    public UpdateBuyerMainAddressService(UserRepositoryPort repositoryPort) {
        if (repositoryPort == null) {
            throw new IllegalArgumentException("UserRepositoryPort cannot be null.");
        }
        this.repositoryPort = repositoryPort;
    }

    public Buyer updateMainAddress(String buyerId, Address newMainAddress) {
        Buyer buyer = loadBuyer(repositoryPort, buyerId);
        buyer.updateMainAddress(newMainAddress);
        return (Buyer) repositoryPort.save(buyer);
    }

    static Buyer loadBuyer(UserRepositoryPort port, String buyerId) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        User user = port.findById(buyerId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found with identifier: " + buyerId));
        if (!(user instanceof Buyer buyer)) {
            throw new InvalidDomainStateException("User '" + buyerId + "' is not a Buyer.");
        }
        return buyer;
    }
}
