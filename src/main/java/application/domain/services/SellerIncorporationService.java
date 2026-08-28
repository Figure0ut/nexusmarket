package application.domain.services;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;

public class SellerIncorporationService {

    private final UserRepositoryPort userRepositoryPort;

    public SellerIncorporationService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public void incorporateSeller(Seller seller, User adminUser) {
        seller.incorporate(adminUser);
        userRepositoryPort.save(seller);
    }
}
