package application.domain.services;

import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;
import application.domain.exceptions.DomainException;
import application.domain.models.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CatalogManagementServiceTest {

    private final MarketplaceFixture f = new MarketplaceFixture().withCatalog();
    private final GetPublishedCatalogService catalog = new GetPublishedCatalogService(f.products);
    private final SuspendProductService suspend = new SuspendProductService(f.products);

    @Test
    @DisplayName("Product ID and SKU must be unique")
    void uniqueSku() {
        assertThrows(DomainException.class, () -> f.registerProduct.registerProduct(
                new Product("P-NEW", "SKU-LAPTOP", "SEL-1", "Copy", 10.0, ProductType.PHYSICAL, ProductStatus.PUBLISHED)));
        assertThrows(DomainException.class, () -> f.registerProduct.registerProduct(
                new Product("P-LAPTOP", "SKU-NEW", "SEL-1", "Copy", 10.0, ProductType.PHYSICAL, ProductStatus.PUBLISHED)));
    }

    @Test
    @DisplayName("Only PUBLISHED products are visible in the public catalog")
    void publicCatalog() {
        assertEquals(2, catalog.getPublishedCatalog().size());
        suspend.suspendProduct("P-EBOOK");
        assertEquals(1, catalog.getPublishedCatalog().size());
        assertEquals("P-LAPTOP", catalog.getPublishedCatalog().get(0).getProductId());
    }
}
