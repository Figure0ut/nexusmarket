package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.Product;
import application.domain.services.*;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.SKU;
import application.infrastructure.adapters.in.rest.dto.AddVariantRequestDto;
import application.infrastructure.adapters.in.rest.dto.ProductRequestDto;
import application.infrastructure.adapters.in.rest.dto.ProductResponseDto;
import application.infrastructure.adapters.in.rest.dto.UpdatePriceRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final RegisterProductService registerProductService;
    private final GetProductService getProductService;
    private final PublishProductService publishProductService;
    private final SuspendProductService suspendProductService;
    private final DiscontinueProductService discontinueProductService;
    private final UpdateProductPriceService updateProductPriceService;
    private final AddProductVariantService addProductVariantService;

    public ProductController(RegisterProductService registerProductService,
                             GetProductService getProductService,
                             PublishProductService publishProductService,
                             SuspendProductService suspendProductService,
                             DiscontinueProductService discontinueProductService,
                             UpdateProductPriceService updateProductPriceService,
                             AddProductVariantService addProductVariantService) {
        this.registerProductService = registerProductService;
        this.getProductService = getProductService;
        this.publishProductService = publishProductService;
        this.suspendProductService = suspendProductService;
        this.discontinueProductService = discontinueProductService;
        this.updateProductPriceService = updateProductPriceService;
        this.addProductVariantService = addProductVariantService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> registerProduct(@RequestBody ProductRequestDto dto) {
        Product product = new Product(
                dto.getProductId(),
                new SKU(dto.getSku()),
                dto.getSellerId(),
                dto.getName(),
                dto.getDescription(),
                new Money(dto.getPrice()),
                dto.getType(),
                dto.getStatus(),
                new ArrayList<>()
        );
        Product registered = registerProductService.registerProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponseDto.fromDomain(registered));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable String productId) {
        Product product = getProductService.getProduct(productId);
        return ResponseEntity.ok(ProductResponseDto.fromDomain(product));
    }

    @PutMapping("/{productId}/publish")
    public ResponseEntity<Void> publishProduct(@PathVariable String productId) {
        publishProductService.publishProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}/suspend")
    public ResponseEntity<Void> suspendProduct(@PathVariable String productId) {
        suspendProductService.suspendProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}/discontinue")
    public ResponseEntity<Void> discontinueProduct(@PathVariable String productId) {
        discontinueProductService.discontinueProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}/price")
    public ResponseEntity<Void> updatePrice(@PathVariable String productId,
                                            @RequestBody UpdatePriceRequestDto dto) {
        updateProductPriceService.updatePrice(productId, new Money(dto.getNewPrice()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/variants")
    public ResponseEntity<Void> addVariant(@PathVariable String productId,
                                           @RequestBody AddVariantRequestDto dto) {
        addProductVariantService.addVariant(productId, new ProductVariant(dto.getName(), dto.getValue()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
