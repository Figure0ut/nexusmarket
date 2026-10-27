package application.infrastructure.adapters.in.rest.controllers;

import application.domain.enums.UserRole;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.services.*;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.TaxIdentifier;
import application.infrastructure.adapters.in.rest.dto.UserRequestDto;
import application.infrastructure.adapters.in.rest.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final RegisterUserService registerUserService;
    private final AuthenticateUserService authenticateUserService;
    private final BlockUserService blockUserService;
    private final ActivateUserService activateUserService;
    private final GetUserService getUserService;

    public UserController(RegisterUserService registerUserService,
                          AuthenticateUserService authenticateUserService,
                          BlockUserService blockUserService,
                          ActivateUserService activateUserService,
                          GetUserService getUserService) {
        this.registerUserService = registerUserService;
        this.authenticateUserService = authenticateUserService;
        this.blockUserService = blockUserService;
        this.activateUserService = activateUserService;
        this.getUserService = getUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto dto) {
        User user;
        if (dto.getRole() == UserRole.BUYER) {
            user = new Buyer(
                    dto.getIdentifier(),
                    dto.getFullName(),
                    dto.getEmail(),
                    dto.getMainAddress() != null ? dto.getMainAddress() : "Default Address",
                    dto.getCommercialStatus() != null ? dto.getCommercialStatus() : "ACTIVE"
            );
        } else if (dto.getRole() == UserRole.SELLER) {
            user = new Seller(
                    dto.getIdentifier(),
                    dto.getFullName(),
                    new Email(dto.getEmail()),
                    dto.getRole(),
                    dto.getStatus(),
                    dto.getTaxIdentifier() != null ? new TaxIdentifier(dto.getTaxIdentifier()) : new TaxIdentifier("DEFAULT-TAX"),
                    dto.getCorporateName() != null ? dto.getCorporateName() : dto.getFullName()
            );
        } else {
            user = new User(
                    dto.getIdentifier(),
                    dto.getFullName(),
                    new Email(dto.getEmail()),
                    dto.getRole(),
                    dto.getStatus()
            );
        }

        User registered = registerUserService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.fromDomain(registered));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserResponseDto> authenticate(@RequestParam String email) {
        User user = authenticateUserService.authenticate(new Email(email));
        return ResponseEntity.ok(UserResponseDto.fromDomain(user));
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String identifier) {
        User user = getUserService.getUser(identifier);
        return ResponseEntity.ok(UserResponseDto.fromDomain(user));
    }

    @PutMapping("/{identifier}/block")
    public ResponseEntity<Void> blockUser(@PathVariable String identifier) {
        blockUserService.blockUser(identifier);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{identifier}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable String identifier) {
        activateUserService.activateUser(identifier);
        return ResponseEntity.noContent().build();
    }
}
