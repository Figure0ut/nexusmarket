package application.infrastructure.adapters.out.memory;

import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.SKU;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryProductRepositoryAdapter implements ProductRepositoryPort {

    private final Map<String, Product> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<Product> findById(String productId) {
        return Optional.ofNullable(storage.get(productId));
    }

    @Override
    public Optional<Product> findBySku(SKU sku) {
        return storage.values().stream()
                .filter(p -> p.getSku().equals(sku))
                .findFirst();
    }

    @Override
    public List<Product> findBySellerId(String sellerId) {
        return storage.values().stream()
                .filter(p -> p.getSellerId().equals(sellerId))
                .toList();
    }

    @Override
    public Product save(Product product) {
        storage.put(product.getProductId(), product);
        return product;
    }
}
