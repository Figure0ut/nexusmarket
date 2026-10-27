package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.User;
import application.domain.services.GetUserService;
import application.domain.services.IncorporateSellerService;
import application.infrastructure.adapters.in.rest.dto.IncorporateSellerRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final IncorporateSellerService incorporateSellerService;
    private final GetUserService getUserService;

    public SellerController(IncorporateSellerService incorporateSellerService, GetUserService getUserService) {
        this.incorporateSellerService = incorporateSellerService;
        this.getUserService = getUserService;
    }

    @PostMapping("/{sellerId}/incorporate")
    public ResponseEntity<Void> incorporateSeller(@PathVariable String sellerId,
                                                  @RequestBody IncorporateSellerRequestDto request) {
        User admin = getUserService.getUser(request.getAdminIdentifier());
        incorporateSellerService.incorporateSeller(sellerId, admin);
        return ResponseEntity.noContent().build();
    }
}
