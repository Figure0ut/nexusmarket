package application.infrastructure.adapters.out.sql.repository;

import application.infrastructure.adapters.out.sql.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataJpaCartRepository extends JpaRepository<CartEntity, String> {
    Optional<CartEntity> findByBuyerId(String buyerId);
}
