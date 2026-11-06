package application.infrastructure.adapters.out.mongo;

import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.SKU;
import application.infrastructure.adapters.out.mongo.document.ProductDocument;
import application.infrastructure.adapters.out.mongo.repository.SpringDataMongoProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB Outbound Persistence Adapter for Product Catalog.
 * Satisfies ProductRepositoryPort using Spring Data MongoDB documents.
 */
@Component
@Primary
public class MongoProductRepositoryAdapter implements ProductRepositoryPort {

    private final SpringDataMongoProductRepository mongoRepository;

    public MongoProductRepositoryAdapter(SpringDataMongoProductRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Optional<Product> findById(String productId) {
        if (productId == null) return Optional.empty();
        return mongoRepository.findById(productId).map(this::toDomain);
    }

    @Override
    public Optional<Product> findBySku(SKU sku) {
        if (sku == null) return Optional.empty();
        return mongoRepository.findBySku(sku.getCode()).map(this::toDomain);
    }

    @Override
    public List<Product> findBySellerId(String sellerId) {
        if (sellerId == null) return List.of();
        return mongoRepository.findBySellerId(sellerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Product> findAll() {
        return mongoRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Product save(Product product) {
        ProductDocument doc = toDocument(product);
        ProductDocument saved = mongoRepository.save(doc);
        return toDomain(saved);
    }

    private Product toDomain(ProductDocument doc) {
        if (doc == null) return null;
        List<ProductVariant> variants = new ArrayList<>();
        if (doc.getVariants() != null) {
            for (ProductDocument.ProductVariantDocument v : doc.getVariants()) {
                variants.add(new ProductVariant(v.getName(), v.getValue()));
            }
        }
        return new Product(
                doc.getProductId(),
                new SKU(doc.getSku()),
                doc.getSellerId(),
                doc.getName(),
                doc.getDescription(),
                new Money(doc.getPrice()),
                doc.getProductType(),
                doc.getStatus(),
                variants
        );
    }

    private ProductDocument toDocument(Product product) {
        if (product == null) return null;
        ProductDocument doc = new ProductDocument();
        doc.setProductId(product.getProductId());
        doc.setSku(product.getSku() != null ? product.getSku().getCode() : null);
        doc.setSellerId(product.getSellerId());
        doc.setName(product.getName());
        doc.setDescription(product.getDescription());
        doc.setPrice(product.getPrice() != null ? product.getPrice().getAmount().doubleValue() : 0.0);
        doc.setProductType(product.getProductType());
        doc.setStatus(product.getStatus());

        List<ProductDocument.ProductVariantDocument> variants = new ArrayList<>();
        if (product.getVariants() != null) {
            for (ProductVariant v : product.getVariants()) {
                variants.add(new ProductDocument.ProductVariantDocument(v.getName(), v.getValue()));
            }
        }
        doc.setVariants(variants);
        return doc;
    }
}
