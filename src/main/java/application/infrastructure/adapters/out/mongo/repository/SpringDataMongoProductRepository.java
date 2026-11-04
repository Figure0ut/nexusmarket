package application.infrastructure.adapters.out.mongo.repository;

import application.infrastructure.adapters.out.mongo.document.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataMongoProductRepository extends MongoRepository<ProductDocument, String> {
    Optional<ProductDocument> findBySku(String sku);
    List<ProductDocument> findBySellerId(String sellerId);
}
