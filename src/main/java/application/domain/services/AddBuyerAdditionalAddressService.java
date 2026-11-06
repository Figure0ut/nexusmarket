package application.domain.services;

import application.domain.models.Buyer;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Address;

/**
 * Atomic domain service responsible exclusively for adding a secondary delivery address to a buyer (Domain 2).
 */
public class AddBuyerAdditionalAddressService {

    private final UserRepositoryPort repositoryPort;

    public AddBuyerAdditionalAddressService(UserRepositoryPort repositoryPort) {
        if (repositoryPort == null) {
            throw new IllegalArgumentException("UserRepositoryPort cannot be null.");
        }
        this.repositoryPort = repositoryPort;
    }

    public Buyer addAdditionalAddress(String buyerId, Address address) {
        Buyer buyer = UpdateBuyerMainAddressService.loadBuyer(repositoryPort, buyerId);
        buyer.addSecondaryAddress(address);
        return (Buyer) repositoryPort.save(buyer);
    }
}
