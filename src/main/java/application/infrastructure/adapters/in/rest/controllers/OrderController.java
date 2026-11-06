package application.infrastructure.adapters.in.rest.controllers;

import application.domain.enums.UserRole;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.services.CheckoutCartService;
import application.domain.services.GetAllOrdersService;
import application.domain.services.GetOrderService;
import application.domain.services.GetOrdersByBuyerService;
import application.domain.services.GetProductsBySellerService;
import application.domain.services.ProcessOrderPaymentService;
import application.domain.services.ValidateAuthorizationService;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import application.domain.valueobjects.Address;
import application.infrastructure.adapters.in.rest.dto.CheckoutRequestDto;
import application.infrastructure.adapters.in.rest.dto.OrderResponseDto;
import application.infrastructure.adapters.in.rest.dto.PaymentRequestDto;
import application.infrastructure.adapters.in.rest.security.AuditTrail;
import application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static application.infrastructure.adapters.in.rest.security.AuthenticatedUserResolver.USER_EMAIL_HEADER;

/**
 * Domain 7 (Order Management). Matrix "Order management": Buyer, Seller and Logistics Operator.
 * <ul>
 *   <li>Buyer: confirms its cart (checkout), pays and consults its own orders.</li>
 *   <li>Seller: consults the orders that contain its products.</li>
 *   <li>Logistics Operator: consults every order to prepare and dispatch them.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CheckoutCartService checkoutCartService;
    private final ProcessOrderPaymentService processOrderPaymentService;
    private final GetOrderService getOrderService;
    private final GetOrdersByBuyerService getOrdersByBuyerService;
    private final GetAllOrdersService getAllOrdersService;
    private final GetProductsBySellerService getProductsBySellerService;
    private final ValidateAuthorizationService authorization;
    private final AuthenticatedUserResolver authenticatedUserResolver;
    private final AuditTrail auditTrail;

    public OrderController(CheckoutCartService checkoutCartService,
                           ProcessOrderPaymentService processOrderPaymentService,
                           GetOrderService getOrderService,
                           GetOrdersByBuyerService getOrdersByBuyerService,
                           GetAllOrdersService getAllOrdersService,
                           GetProductsBySellerService getProductsBySellerService,
                           ValidateAuthorizationService authorization,
                           AuthenticatedUserResolver authenticatedUserResolver,
                           AuditTrail auditTrail) {
        this.checkoutCartService = checkoutCartService;
        this.processOrderPaymentService = processOrderPaymentService;
        this.getOrderService = getOrderService;
        this.getOrdersByBuyerService = getOrdersByBuyerService;
        this.getAllOrdersService = getAllOrdersService;
        this.getProductsBySellerService = getProductsBySellerService;
        this.authorization = authorization;
        this.authenticatedUserResolver = authenticatedUserResolver;
        this.auditTrail = auditTrail;
    }

    /**
     * CART -> PENDING_PAYMENT. Stock of physical products is reserved.
     */
    @PostMapping("/checkout/{buyerId}")
    public ResponseEntity<OrderResponseDto> checkoutCart(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String buyerId, @RequestBody CheckoutRequestDto dto) {
        User buyer = authorizeBuyer(email, buyerId);
        Address shippingAddress = (dto.shippingAddress() != null && !dto.shippingAddress().trim().isEmpty())
                ? new Address(dto.shippingAddress())
                : ((Buyer) buyer).getMainAddress();
        Order order = checkoutCartService.checkoutCart(buyerId, dto.orderId(), shippingAddress);
        auditTrail.record(buyer, "CHECKOUT", order.getOrderId(), "Status: " + order.getStatus() + ", total: " + order.getTotalAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponseDto.fromDomain(order));
    }

    /**
     * PENDING_PAYMENT -> PAID (or DELIVERED_FINALIZED for digital-only orders). Issues the invoice.
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderResponseDto> payOrder(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String orderId, @RequestBody PaymentRequestDto dto) {
        User buyer = authenticatedUserResolver.resolve(email);
        Order order = getOrderService.getOrder(orderId);
        authorizeBuyer(buyer, order.getBuyerId());
        Order paid = processOrderPaymentService.processOrderPayment(orderId, dto.paymentToken(), buyer);
        auditTrail.record(buyer, "PAY_ORDER", orderId, "Status: " + paid.getStatus());
        return ResponseEntity.ok(OrderResponseDto.fromDomain(paid));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String orderId) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateQueryAuthorization(actor, BusinessProcess.ORDER_MANAGEMENT);
        Order order = getOrderService.getOrder(orderId);
        if (!visibleOrders(actor, List.of(order)).contains(order)) {
            throw new application.domain.exceptions.AuthorizationException("Authorization Failure (RG-03): User '"
                    + actor.getIdentifier() + "' cannot consult order '" + orderId + "'.");
        }
        return ResponseEntity.ok(OrderResponseDto.fromDomain(order));
    }

    /**
     * Orders visible to the authenticated user: buyers see their own orders, sellers the orders with their
     * products, logistics operators and supervisors every order.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateQueryAuthorization(actor, BusinessProcess.ORDER_MANAGEMENT);
        List<Order> orders = actor.getRole() == UserRole.BUYER
                ? getOrdersByBuyerService.getOrdersByBuyer(actor.getIdentifier())
                : visibleOrders(actor, getAllOrdersService.getAllOrders());
        return ResponseEntity.ok(orders.stream().map(OrderResponseDto::fromDomain).toList());
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByBuyer(@RequestHeader(value = USER_EMAIL_HEADER, required = false) String email, @PathVariable String buyerId) {
        User actor = authenticatedUserResolver.resolve(email);
        authorization.validateQueryAuthorization(actor, BusinessProcess.ORDER_MANAGEMENT);
        authorization.validateOwnership(actor, buyerId);
        List<Order> orders = visibleOrders(actor, getOrdersByBuyerService.getOrdersByBuyer(buyerId));
        return ResponseEntity.ok(orders.stream().map(OrderResponseDto::fromDomain).toList());
    }

    private List<Order> visibleOrders(User actor, List<Order> orders) {
        return switch (actor.getRole()) {
            case BUYER -> orders.stream().filter(o -> o.getBuyerId().equals(actor.getIdentifier())).toList();
            case SELLER -> {
                Set<String> ownProducts = getProductsBySellerService.getProductsBySeller(actor.getIdentifier()).stream()
                        .map(p -> p.getProductId()).collect(Collectors.toSet());
                yield orders.stream().filter(o -> o.getItems().stream().anyMatch(i -> ownProducts.contains(i.getProductId()))).toList();
            }
            default -> orders;
        };
    }

    private User authorizeBuyer(String email, String buyerId) {
        return authorizeBuyer(authenticatedUserResolver.resolve(email), buyerId);
    }

    private User authorizeBuyer(User actor, String buyerId) {
        authorization.validateProcessAuthorization(actor, BusinessProcess.ORDER_MANAGEMENT);
        if (actor.getRole() != UserRole.BUYER) {
            throw new application.domain.exceptions.AuthorizationException("Only the buyer can confirm and pay its own orders.");
        }
        authorization.validateOwnership(actor, buyerId);
        return actor;
    }
}
