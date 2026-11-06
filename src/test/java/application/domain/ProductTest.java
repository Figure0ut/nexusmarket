package application.domain;

import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;
import application.domain.models.Product;
import application.domain.valueobjects.ProductVariant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    @DisplayName("Product status only accepts PUBLISHED, SUSPENDED and DISCONTINUED")
    void statusCatalog() {
        assertArrayEquals(new ProductStatus[]{ProductStatus.PUBLISHED, ProductStatus.SUSPENDED, ProductStatus.DISCONTINUED},
                ProductStatus.values());
    }

    @Test
    @DisplayName("Lifecycle: suspended -> published -> suspended -> discontinued (cannot be published again)")
    void lifecycle() {
        Product product = new Product("P-1", "SKU-1", "SEL-1", "Laptop", 100.0, ProductType.PHYSICAL, ProductStatus.SUSPENDED);
        assertFalse(product.isPublished());
        product.publish();
        assertTrue(product.isPublished());
        product.suspend();
        assertEquals(ProductStatus.SUSPENDED, product.getStatus());
        product.discontinue();
        assertThrows(IllegalStateException.class, product::publish);
    }

    @Test
    @DisplayName("Variants (color, size, model...) are stored as a list without duplicates")
    void variants() {
        Product product = new Product("P-1", "SKU-1", "SEL-1", "Shirt", 20.0, ProductType.PHYSICAL, ProductStatus.PUBLISHED);
        product.addVariant(new ProductVariant("Color", "Red"));
        product.addVariant(new ProductVariant("Size", "M"));
        product.addVariant(new ProductVariant("Color", "Red"));
        assertEquals(2, product.getVariants().size());
    }

    @Test
    @DisplayName("Physical products require inventory; digital products do not")
    void productType() {
        assertTrue(new Product("P-1", "SKU-1", "S", "Book", 10.0, ProductType.PHYSICAL, ProductStatus.PUBLISHED).isPhysical());
        assertFalse(new Product("P-2", "SKU-2", "S", "E-book", 5.0, ProductType.DIGITAL, ProductStatus.PUBLISHED).isPhysical());
    }

    @Test
    @DisplayName("Price must be greater than zero")
    void priceGreaterThanZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product("P-1", "SKU-1", "S", "Free", 0.0, ProductType.DIGITAL, ProductStatus.PUBLISHED));
    }
}
