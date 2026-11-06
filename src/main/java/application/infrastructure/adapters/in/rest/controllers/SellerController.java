package application.infrastructure.adapters.in.rest.controllers;

import application.domain.enums.UserStatus;
import application.domain.enums.WarehouseType;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.services.GetProductsBySellerService;
import application.domain.services.IncorporateSellerService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.TaxIdentifier;
import application.infrastructure.adapters.in.rest.dto.IncorporateSellerRequestDto;
import application.infrastructure.adapters.in.rest.dto.ProductResponseDto;
import application.infrastructure.adapters.in.rest.dto.UserResponseDto;
import application.infrastructure.adapters.in.rest.security.AuditTrail;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Domain 3 (Seller Management). Sellers cannot self-register: the Administrator incorporates them
 * together with their first warehouse (section 6.1, step 1).
 */
@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final IncorporateSellerService incorporateSellerService;
    private final GetProductsBySellerService getProductsBySellerService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final AuditTrail auditTrail;

    public SellerController(IncorporateSellerService incorporateSellerService,
                            GetProductsBySellerService getProductsBySellerService,
                            ValidateAuthorizationService authorization,
                            AuthenticatedUserResolver authenticatedUserResolver,
                            AuditTrail auditTrail) {
        this.incorporateSellerService = incorporateSellerService;
        this.getProductsBySellerService = getProductsBySellerService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
        this.auditTrail = auditTrail;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> incorporateSeller(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email,
                                                             @RequestBody IncorporateSellerRequestDto dto) {
        User admin = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(admin, BusinessProcess.SELLER_REGISTRATION);
        if (dto.firstWarehouse() == null) {
            throw new IllegalArgumentException("The first warehouse of the seller (firstWarehouse) is mandatory.");
        }
        Seller seller = new Seller(dto.identifier(), dto.identityDocument(), dto.fullName(), new Email(dto.email()),
                UserStatus.PENDING_INCORPORATION,
                dto.taxIdentifier() != null ? new TaxIdentifier(dto.taxIdentifier()) : null,
                dto.corporateName());
        Warehouse firstWarehouse = new Warehouse(dto.firstWarehouse().warehouseId(), dto.firstWarehouse().name(),
                new Address(dto.firstWarehouse().location()), WarehouseType.SELLER, seller.getIdentifier(), true);
        Seller incorporated = incorporateSellerService.incorporateSeller(seller, firstWarehouse, admin);
        auditTrail.record(admin, "INCORPORATE_SELLER", incorporated.getIdentifier(),
                "First warehouse: " + firstWarehouse.getWarehouseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.fromDomain(incorporated));
    }

    /**
     * Products of a seller in any status. A seller only sees its own products.
     */
    @GetMapping("/{sellerId}/products")
    public ResponseEntity<List<ProductResponseDto>> getSellerProducts(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email,
                                                                      @PathVariable String sellerId) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateQueryAuthorization(actor, BusinessProcess.PRODUCT_REGISTRATION);
        authorization.validateOwnership(actor, sellerId);
        List<Product> products = getProductsBySellerService.getProductsBySeller(sellerId);
        return ResponseEntity.ok(products.stream().map(ProductResponseDto::fromDomain).toList());
    }
}
