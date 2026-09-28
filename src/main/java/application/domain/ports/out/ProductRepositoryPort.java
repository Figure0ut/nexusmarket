package application.domain.ports.out;

import application.domain.models.Product;
import application.domain.valueobjects.SKU;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Optional<Product> findById(String productId);
    Optional<Product> findBySku(SKU sku);
    List<Product> findBySellerId(String sellerId);
    Product save(Product product);
}
