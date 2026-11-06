package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.services.AddProductVariantService;
import application.domain.services.DiscontinueProductService;
import application.domain.services.GetProductService;
import application.domain.services.GetPublishedCatalogService;
import application.domain.services.PublishProductService;
import application.domain.services.RegisterProductService;
import application.domain.services.SuspendProductService;
import application.domain.services.UpdateProductPriceService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.SKU;
import application.infrastructure.adapters.in.rest.dto.ProductRequestDto;
import application.infrastructure.adapters.in.rest.dto.ProductResponseDto;
import application.infrastructure.adapters.in.rest.dto.UpdatePriceRequestDto;
import application.infrastructure.adapters.in.rest.dto.VariantRequestDto;
import application.infrastructure.adapters.in.rest.security.AuditTrail;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Domain 5 (Catalog Management). Products are registered and administered only by their seller
 * (matrix: "Product registration: Seller"). The public catalog shows PUBLISHED products only.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final RegisterProductService registerProductService;
    private final GetProductService getProductService;
    private final GetPublishedCatalogService getPublishedCatalogService;
    private final PublishProductService publishProductService;
    private final SuspendProductService suspendProductService;
    private final DiscontinueProductService discontinueProductService;
    private final UpdateProductPriceService updateProductPriceService;
    private final AddProductVariantService addProductVariantService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final AuditTrail auditTrail;

    public ProductController(RegisterProductService registerProductService,
                             GetProductService getProductService,
                             GetPublishedCatalogService getPublishedCatalogService,
                             PublishProductService publishProductService,
                             SuspendProductService suspendProductService,
                             DiscontinueProductService discontinueProductService,
                             UpdateProductPriceService updateProductPriceService,
                             AddProductVariantService addProductVariantService,
                             ValidateAuthorizationService authorization,
                             AuthenticatedUserResolver authenticatedUserResolver,
                             AuditTrail auditTrail) {
        this.registerProductService = registerProductService;
        this.getProductService = getProductService;
        this.getPublishedCatalogService = getPublishedCatalogService;
        this.publishProductService = publishProductService;
        this.suspendProductService = suspendProductService;
        this.discontinueProductService = discontinueProductService;
        this.updateProductPriceService = updateProductPriceService;
        this.addProductVariantService = addProductVariantService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
        this.auditTrail = auditTrail;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> registerProduct(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @RequestBody ProductRequestDto dto) {
        User seller = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(seller, BusinessProcess.PRODUCT_REGISTRATION);
        if (dto.status() == null) {
            throw new IllegalArgumentException("Product status is mandatory: PUBLISHED, SUSPENDED or DISCONTINUED.");
        }
        List<ProductVariant> variants = new ArrayList<>();
        if (dto.variants() != null) {
            for (VariantRequestDto v : dto.variants()) {
                variants.add(new ProductVariant(v.name(), v.value()));
            }
        }
        Product product = new Product(dto.productId(), new SKU(dto.sku()), seller.getIdentifier(), dto.name(),
                dto.description(), new Money(dto.price()), dto.type(), dto.status(), variants);
        Product registered = registerProductService.registerProduct(product);
        auditTrail.record(seller, "REGISTER_PRODUCT", registered.getProductId(), "Status: " + registered.getStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponseDto.fromDomain(registered));
    }

    /**
     * Public catalog: only PUBLISHED products are visible (section 6.1, step 4).
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getPublicCatalog(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email) {
        authenticatedUserResolver.resolve(email);
        return ResponseEntity.ok(getPublishedCatalogService.getPublishedCatalog().stream().map(ProductResponseDto::fromDomain).toList());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String productId) {
        User actor = authenticatedUserResolver.resolve(email);
        Product product = getProductService.getProduct(productId);
        if (!product.isPublished()) {
            // Non-published products are not part of the public catalog: only their seller (or the supervisor) can see them.
            authorization.validateQueryAuthorization(actor, BusinessProcess.PRODUCT_REGISTRATION);
            authorization.validateOwnership(actor, product.getSellerId());
        }
        return ResponseEntity.ok(ProductResponseDto.fromDomain(product));
    }

    @PutMapping("/{productId}/publish")
    public ResponseEntity<ProductResponseDto> publishProduct(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String productId) {
        User seller = authorizeOwner(email, productId);
        publishProductService.publishProduct(productId);
        auditTrail.record(seller, "PUBLISH_PRODUCT", productId, "Status: PUBLISHED");
        return ResponseEntity.ok(ProductResponseDto.fromDomain(getProductService.getProduct(productId)));
    }

    @PutMapping("/{productId}/suspend")
    public ResponseEntity<ProductResponseDto> suspendProduct(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String productId) {
        User seller = authorizeOwner(email, productId);
        suspendProductService.suspendProduct(productId);
        auditTrail.record(seller, "SUSPEND_PRODUCT", productId, "Status: SUSPENDED");
        return ResponseEntity.ok(ProductResponseDto.fromDomain(getProductService.getProduct(productId)));
    }

    @PutMapping("/{productId}/discontinue")
    public ResponseEntity<ProductResponseDto> discontinueProduct(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String productId) {
        User seller = authorizeOwner(email, productId);
        discontinueProductService.discontinueProduct(productId);
        auditTrail.record(seller, "DISCONTINUE_PRODUCT", productId, "Status: DISCONTINUED");
        return ResponseEntity.ok(ProductResponseDto.fromDomain(getProductService.getProduct(productId)));
    }

    @PutMapping("/{productId}/price")
    public ResponseEntity<ProductResponseDto> updatePrice(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String productId,
                                                          @RequestBody UpdatePriceRequestDto dto) {
        User seller = authorizeOwner(email, productId);
        updateProductPriceService.updatePrice(productId, new Money(dto.newPrice()));
        auditTrail.record(seller, "UPDATE_PRODUCT_PRICE", productId, "New price: " + dto.newPrice());
        return ResponseEntity.ok(ProductResponseDto.fromDomain(getProductService.getProduct(productId)));
    }

    @PostMapping("/{productId}/variants")
    public ResponseEntity<ProductResponseDto> addVariant(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String productId,
                                                         @RequestBody VariantRequestDto dto) {
        User seller = authorizeOwner(email, productId);
        addProductVariantService.addVariant(productId, new ProductVariant(dto.name(), dto.value()));
        auditTrail.record(seller, "ADD_PRODUCT_VARIANT", productId, dto.name() + "=" + dto.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponseDto.fromDomain(getProductService.getProduct(productId)));
    }

    private User authorizeOwner(String email, String productId) {
        User seller = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(seller, BusinessProcess.PRODUCT_REGISTRATION);
        authorization.validateOwnership(seller, getProductService.getProduct(productId).getSellerId());
        return seller;
    }
}
