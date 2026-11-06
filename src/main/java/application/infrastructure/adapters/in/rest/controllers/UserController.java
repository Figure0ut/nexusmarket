package application.infrastructure.adapters.in.rest.controllers;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.services.ActivateUserService;
import application.domain.services.AddBuyerAdditionalAddressService;
import application.domain.services.BlockUserService;
import application.domain.services.GetAllUsersService;
import application.domain.services.GetUserService;
import application.domain.services.RegisterUserService;
import application.domain.services.UpdateBuyerMainAddressService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.infrastructure.adapters.in.rest.dto.AddressRequestDto;
import application.infrastructure.adapters.in.rest.dto.BuyerRegistrationRequestDto;
import application.infrastructure.adapters.in.rest.dto.InternalUserRequestDto;
import application.infrastructure.adapters.in.rest.dto.UserResponseDto;
import application.infrastructure.adapters.in.rest.security.AuditTrail;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Domain 1 (User Administration) and Domain 2 (Buyer Management).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final RegisterUserService registerUserService;
    private final BlockUserService blockUserService;
    private final ActivateUserService activateUserService;
    private final GetUserService getUserService;
    private final GetAllUsersService getAllUsersService;
    private final UpdateBuyerMainAddressService updateBuyerMainAddressService;
    private final AddBuyerAdditionalAddressService addBuyerAdditionalAddressService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final AuditTrail auditTrail;

    public UserController(RegisterUserService registerUserService,
                          BlockUserService blockUserService,
                          ActivateUserService activateUserService,
                          GetUserService getUserService,
                          GetAllUsersService getAllUsersService,
                          UpdateBuyerMainAddressService updateBuyerMainAddressService,
                          AddBuyerAdditionalAddressService addBuyerAdditionalAddressService,
                          ValidateAuthorizationService authorization,
                          AuthenticatedUserResolver authenticatedUserResolver,
                          AuditTrail auditTrail) {
        this.registerUserService = registerUserService;
        this.blockUserService = blockUserService;
        this.activateUserService = activateUserService;
        this.getUserService = getUserService;
        this.getAllUsersService = getAllUsersService;
        this.updateBuyerMainAddressService = updateBuyerMainAddressService;
        this.addBuyerAdditionalAddressService = addBuyerAdditionalAddressService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
        this.auditTrail = auditTrail;
    }

    /**
     * Buyer self-registration ("Buyer registration"). This is the entry point that creates the
     * account used later to authenticate, so it is the only operation (besides login) without an authenticated user.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerBuyer(@RequestBody BuyerRegistrationRequestDto dto) {
        List<Address> additional = new ArrayList<>();
        if (dto.additionalAddresses() != null) {
            dto.additionalAddresses().forEach(a -> additional.add(new Address(a)));
        }
        Buyer buyer = new Buyer(dto.identifier(), dto.identityDocument(), dto.fullName(), new Email(dto.email()),
                UserStatus.ACTIVE, new Address(dto.mainAddress()), additional, dto.commercialStatus());
        User registered = registerUserService.registerUser(buyer);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.fromDomain(registered));
    }

    /**
     * Registration of internal users (ADMIN, OPERATOR_LOGISTIC, SUPERVISOR) by the Administrator.
     * Sellers are incorporated through /api/sellers and buyers self-register through /api/users/register.
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> registerInternalUser(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email,
                                                                @RequestBody InternalUserRequestDto dto) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(actor, BusinessProcess.USER_ADMINISTRATION);
        if (dto.role() == UserRole.SELLER) {
            throw new InvalidDomainStateException("Sellers are incorporated with their first warehouse through POST /api/sellers.");
        }
        if (dto.role() == UserRole.BUYER) {
            throw new InvalidDomainStateException("Buyers register themselves through POST /api/users/register.");
        }
        User user = new User(dto.identifier(), dto.identityDocument(), dto.fullName(), new Email(dto.email()), dto.role(), UserStatus.ACTIVE);
        User registered = registerUserService.registerUser(user);
        auditTrail.record(actor, "REGISTER_USER", registered.getIdentifier(), "Role: " + registered.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.fromDomain(registered));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email) {
        return ResponseEntity.ok(UserResponseDto.fromDomain(authenticatedUserResolver.resolve(email)));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateQueryAuthorization(actor, BusinessProcess.USER_ADMINISTRATION);
        return ResponseEntity.ok(getAllUsersService.getAllUsers().stream().map(UserResponseDto::fromDomain).toList());
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<UserResponseDto> getUser(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email,
                                                   @PathVariable String identifier) {
        User actor = authenticatedUserResolver.resolve(email);
        if (!actor.getIdentifier().equals(identifier)) {
            authorization.validateQueryAuthorization(actor, BusinessProcess.USER_ADMINISTRATION);
        }
        return ResponseEntity.ok(UserResponseDto.fromDomain(getUserService.getUser(identifier)));
    }

    @PutMapping("/{identifier}/block")
    public ResponseEntity<UserResponseDto> blockUser(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email,
                                                     @PathVariable String identifier) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(actor, BusinessProcess.USER_ADMINISTRATION);
        if (actor.getIdentifier().equals(identifier)) {
            throw new InvalidDomainStateException("An administrator cannot block its own account.");
        }
        blockUserService.blockUser(identifier);
        auditTrail.record(actor, "BLOCK_USER", identifier, "Status: BLOCKED");
        return ResponseEntity.ok(UserResponseDto.fromDomain(getUserService.getUser(identifier)));
    }

    @PutMapping("/{identifier}/activate")
    public ResponseEntity<UserResponseDto> activateUser(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email,
                                                        @PathVariable String identifier) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(actor, BusinessProcess.USER_ADMINISTRATION);
        activateUserService.activateUser(identifier);
        auditTrail.record(actor, "ACTIVATE_USER", identifier, "Status: ACTIVE");
        return ResponseEntity.ok(UserResponseDto.fromDomain(getUserService.getUser(identifier)));
    }

    /**
     * A buyer only manages its own addresses (Domain 2).
     */
    @PutMapping("/{identifier}/main-address")
    public ResponseEntity<UserResponseDto> updateMainAddress(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email,
                                                             @PathVariable String identifier,
                                                             @RequestBody AddressRequestDto dto) {
        User actor = authenticatedUserResolver.resolve(email);
        validateBuyerSelf(actor, identifier);
        Buyer buyer = updateBuyerMainAddressService.updateMainAddress(identifier, new Address(dto.address()));
        auditTrail.record(actor, "UPDATE_MAIN_ADDRESS", identifier, dto.address());
        return ResponseEntity.ok(UserResponseDto.fromDomain(buyer));
    }

    @PostMapping("/{identifier}/additional-addresses")
    public ResponseEntity<UserResponseDto> addAdditionalAddress(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email,
                                                                @PathVariable String identifier,
                                                                @RequestBody AddressRequestDto dto) {
        User actor = authenticatedUserResolver.resolve(email);
        validateBuyerSelf(actor, identifier);
        Buyer buyer = addBuyerAdditionalAddressService.addAdditionalAddress(identifier, new Address(dto.address()));
        auditTrail.record(actor, "ADD_ADDITIONAL_ADDRESS", identifier, dto.address());
        return ResponseEntity.ok(UserResponseDto.fromDomain(buyer));
    }

    private void validateBuyerSelf(User actor, String identifier) {
        if (actor.getRole() != UserRole.BUYER) {
            throw new application.domain.exceptions.AuthorizationException("Only buyers manage delivery addresses.");
        }
        authorization.validateOwnership(actor, identifier);
    }
}
