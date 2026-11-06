package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.User;
import application.domain.services.GenerateAdministrativeReportService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.infrastructure.adapters.in.rest.dto.AdministrativeReportResponseDto;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Administrative reports (OBJ-12 / "Consulta de reportes administrativos"). Administrator and Supervisor.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final GenerateAdministrativeReportService generateAdministrativeReportService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    public ReportController(GenerateAdministrativeReportService generateAdministrativeReportService,
                            ValidateAuthorizationService authorization,
                            AuthenticatedUserResolver authenticatedUserResolver) {
        this.generateAdministrativeReportService = generateAdministrativeReportService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
    }

    @GetMapping("/summary")
    public ResponseEntity<AdministrativeReportResponseDto> getSummary(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateProcessAuthorization(actor, BusinessProcess.ADMINISTRATIVE_REPORTS);
        return ResponseEntity.ok(AdministrativeReportResponseDto.fromDomain(generateAdministrativeReportService.generateReport()));
    }
}
