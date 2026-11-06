package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.User;
import application.domain.services.AuthenticateUserService;
import application.domain.valueobjects.Email;
import application.infrastructure.adapters.in.rest.dto.LoginRequestDto;
import application.infrastructure.adapters.in.rest.dto.UserResponseDto;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Authentication entry point. Verifies that the email belongs to an ACTIVE user and tells the client
 * which header must be sent in every following request (RG-01).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticateUserService authenticateUserService;

    public AuthController(AuthenticateUserService authenticateUserService) {
        this.authenticateUserService = authenticateUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDto dto) {
        User user = authenticateUserService.authenticate(new Email(dto.email()));
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "header", AuthenticatedUserResolver.USER_EMAIL_HEADER,
                "headerValue", user.getEmail().getValue(),
                "user", UserResponseDto.fromDomain(user)
        ));
    }
}
