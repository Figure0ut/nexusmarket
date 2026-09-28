package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;

public class SellerIncorporationService {

    private final UserRepositoryPort userRepositoryPort;

    public SellerIncorporationService(UserRepositoryPort userRepositoryPort) {
        if (userRepositoryPort == null) {
            throw new IllegalArgumentException("UserRepositoryPort cannot be null.");
        }
        this.userRepositoryPort = userRepositoryPort;
    }

    public void incorporateSeller(String sellerId, User adminUser) {
        if (sellerId == null || sellerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Seller ID cannot be null or empty.");
        }
        User user = userRepositoryPort.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with identifier: " + sellerId));

        if (!(user instanceof Seller)) {
            throw new InvalidDomainStateException("User with identifier '" + sellerId + "' is not a Seller entity.");
        }

        Seller seller = (Seller) user;
        seller.incorporate(adminUser);
        userRepositoryPort.save(seller);
    }

    public void incorporateSeller(Seller seller, User adminUser) {
        if (seller == null) {
            throw new IllegalArgumentException("Seller cannot be null.");
        }
        seller.incorporate(adminUser);
        userRepositoryPort.save(seller);
    }
}
