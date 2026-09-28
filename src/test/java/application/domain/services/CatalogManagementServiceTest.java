package application.domain.services;

import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.SKU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CatalogManagementServiceTest {

    private CatalogManagementService service;
    private InMemoryProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();
        service = new CatalogManagementService(repository);
    }

    @Test
    @DisplayName("Should successfully register product and update lifecycle statuses")
    void shouldRegisterAndManageProductLifecycle() {
        Product product = new Product("P-001", new SKU("SKU-100"), "SEL-1", "Gaming Laptop", "Fast laptop",
                new Money(1200.00), ProductType.PHYSICAL, ProductStatus.DRAFT, new ArrayList<>());

        service.registerProduct(product);
        assertEquals(ProductStatus.DRAFT, service.getProduct("P-001").getStatus());

        service.publishProduct("P-001");
        assertEquals(ProductStatus.PUBLISHED, service.getProduct("P-001").getStatus());

        service.suspendProduct("P-001");
        assertEquals(ProductStatus.SUSPENDED, service.getProduct("P-001").getStatus());

        service.updatePrice("P-001", new Money(1150.00));
        assertEquals(new Money(1150.00), service.getProduct("P-001").getPrice());

        service.addVariant("P-001", new ProductVariant("RAM", "32GB"));
        assertEquals(1, service.getProduct("P-001").getVariants().size());

        service.discontinueProduct("P-001");
        assertEquals(ProductStatus.DISCONTINUED, service.getProduct("P-001").getStatus());
    }

    @Test
    @DisplayName("Should reject duplicate SKU on product registration")
    void shouldRejectDuplicateSKU() {
        Product p1 = new Product("P-001", new SKU("SKU-100"), "SEL-1", "Product 1", "Desc",
                new Money(50.00), ProductType.DIGITAL, ProductStatus.DRAFT, new ArrayList<>());
        Product p2 = new Product("P-002", new SKU("SKU-100"), "SEL-1", "Product 2", "Desc",
                new Money(60.00), ProductType.DIGITAL, ProductStatus.DRAFT, new ArrayList<>());

        service.registerProduct(p1);
        assertThrows(DomainException.class, () -> service.registerProduct(p2));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when querying non-existent product")
    void shouldThrowWhenProductNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.getProduct("NON-EXISTENT"));
    }

    private static class InMemoryProductRepository implements ProductRepositoryPort {
        private final Map<String, Product> storage = new HashMap<>();

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
}
