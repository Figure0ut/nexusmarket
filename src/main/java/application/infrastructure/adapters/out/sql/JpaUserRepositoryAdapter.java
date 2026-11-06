package application.infrastructure.adapters.out.sql;

import application.domain.enums.UserRole;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.TaxIdentifier;
import application.infrastructure.adapters.out.sql.entity.UserEntity;
import application.infrastructure.adapters.out.sql.repository.SpringDataJpaUserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Primary
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataJpaUserRepository jpaRepository;

    public JpaUserRepositoryAdapter(SpringDataJpaUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findById(String identifier) {
        if (identifier == null) return Optional.empty();
        return jpaRepository.findById(identifier).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        if (email == null) return Optional.empty();
        return jpaRepository.findByEmail(email.getValue()).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public boolean existsByEmail(Email email) {
        if (email == null) return false;
        return jpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public boolean existsByIdentityDocument(String identityDocument) {
        if (identityDocument == null) return false;
        return jpaRepository.existsByIdentityDocument(identityDocument.trim());
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private User toDomain(UserEntity entity) {
        if (entity == null) return null;
        if (entity.getRole() == UserRole.BUYER) {
            List<Address> additional = new ArrayList<>();
            if (entity.getAdditionalAddresses() != null) {
                entity.getAdditionalAddresses().forEach(a -> additional.add(new Address(a)));
            }
            return new Buyer(
                    entity.getIdentifier(),
                    entity.getIdentityDocument(),
                    entity.getFullName(),
                    new Email(entity.getEmail()),
                    entity.getStatus(),
                    new Address(entity.getMainAddress()),
                    additional,
                    entity.getCommercialStatus()
            );
        } else if (entity.getRole() == UserRole.SELLER) {
            return new Seller(
                    entity.getIdentifier(),
                    entity.getIdentityDocument(),
                    entity.getFullName(),
                    new Email(entity.getEmail()),
                    entity.getStatus(),
                    entity.getTaxIdentifier() != null ? new TaxIdentifier(entity.getTaxIdentifier()) : null,
                    entity.getCorporateName()
            );
        } else {
            return new User(
                    entity.getIdentifier(),
                    entity.getIdentityDocument(),
                    entity.getFullName(),
                    new Email(entity.getEmail()),
                    entity.getRole(),
                    entity.getStatus()
            );
        }
    }

    private UserEntity toEntity(User user) {
        if (user == null) return null;
        UserEntity entity = new UserEntity();
        entity.setIdentifier(user.getIdentifier());
        entity.setIdentityDocument(user.getIdentityDocument());
        entity.setFullName(user.getFullName());
        entity.setEmail(user.getEmail() != null ? user.getEmail().getValue() : null);
        entity.setRole(user.getRole());
        entity.setStatus(user.getStatus());

        if (user instanceof Buyer buyer) {
            entity.setMainAddress(buyer.getMainAddress().getStreet());
            entity.setCommercialStatus(buyer.getCommercialStatus());
            entity.setAdditionalAddresses(new ArrayList<>(buyer.getAdditionalAddresses().stream().map(Address::getStreet).toList()));
        } else if (user instanceof Seller seller) {
            entity.setTaxIdentifier(seller.getTaxIdentifier() != null ? seller.getTaxIdentifier().getTaxId() : null);
            entity.setCorporateName(seller.getCorporateName());
        }
        return entity;
    }
}
