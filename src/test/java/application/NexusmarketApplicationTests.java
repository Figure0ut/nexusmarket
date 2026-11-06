package application;

import application.domain.services.DispatchShipmentService;
import application.domain.services.GenerateAdministrativeReportService;
import application.domain.services.IncorporateSellerService;
import application.domain.services.CheckoutCartService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class NexusmarketApplicationTests {

    @Autowired private ValidateAuthorizationService validateAuthorizationService;
    @Autowired private IncorporateSellerService incorporateSellerService;
    @Autowired private CheckoutCartService checkoutCartService;
    @Autowired private DispatchShipmentService dispatchShipmentService;
    @Autowired private GenerateAdministrativeReportService generateAdministrativeReportService;
    @Autowired private UserRepositoryPort userRepositoryPort;

    @Test
    @DisplayName("Spring context loads and wires the pure-Java domain services")
    void contextLoads() {
        assertNotNull(validateAuthorizationService);
        assertNotNull(incorporateSellerService);
        assertNotNull(checkoutCartService);
        assertNotNull(dispatchShipmentService);
        assertNotNull(generateAdministrativeReportService);
    }

    @Test
    @DisplayName("The initial Administrator is created at startup")
    void initialAdministratorExists() {
        assertTrue(userRepositoryPort.findById("ADM-001").isPresent());
    }
}
