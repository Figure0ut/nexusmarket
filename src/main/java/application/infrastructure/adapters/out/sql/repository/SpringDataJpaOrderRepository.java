package application.infrastructure.adapters.out.sql.repository;

import application.infrastructure.adapters.out.sql.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataJpaOrderRepository extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByBuyerId(String buyerId);
}
