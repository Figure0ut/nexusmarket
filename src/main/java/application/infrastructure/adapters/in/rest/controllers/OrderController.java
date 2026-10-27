package application.infrastructure.adapters.in.rest.controllers;

import application.domain.models.Order;
import application.domain.services.*;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import application.infrastructure.adapters.in.rest.dto.CheckoutRequestDto;
import application.infrastructure.adapters.in.rest.dto.OrderResponseDto;
import application.infrastructure.adapters.in.rest.dto.PaymentRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CheckoutCartService checkoutCartService;
    private final ProcessOrderPaymentService processOrderPaymentService;
    private final CancelOrderService cancelOrderService;
    private final GetOrderService getOrderService;
    private final GetOrdersByBuyerService getOrdersByBuyerService;

    public OrderController(CheckoutCartService checkoutCartService,
                           ProcessOrderPaymentService processOrderPaymentService,
                           CancelOrderService cancelOrderService,
                           GetOrderService getOrderService,
                           GetOrdersByBuyerService getOrdersByBuyerService) {
        this.checkoutCartService = checkoutCartService;
        this.processOrderPaymentService = processOrderPaymentService;
        this.cancelOrderService = cancelOrderService;
        this.getOrderService = getOrderService;
        this.getOrdersByBuyerService = getOrdersByBuyerService;
    }

    @PostMapping("/checkout/{buyerId}")
    public ResponseEntity<OrderResponseDto> checkoutCart(@PathVariable String buyerId,
                                                         @RequestBody CheckoutRequestDto dto) {
        Order order = checkoutCartService.checkoutCart(buyerId, dto.getOrderId(), new Address(dto.getShippingAddress()));
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponseDto.fromDomain(order));
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Map<String, Object>> payOrder(@PathVariable String orderId,
                                                        @RequestBody PaymentRequestDto dto) {
        Email email = dto.getBuyerEmail() != null ? new Email(dto.getBuyerEmail()) : null;
        boolean success = processOrderPaymentService.processOrderPayment(orderId, dto.getPaymentToken(), email);
        return ResponseEntity.ok(Map.of("orderId", orderId, "paymentSuccess", success));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        cancelOrderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable String orderId) {
        Order order = getOrderService.getOrder(orderId);
        return ResponseEntity.ok(OrderResponseDto.fromDomain(order));
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByBuyer(@PathVariable String buyerId) {
        List<Order> orders = getOrdersByBuyerService.getOrdersByBuyer(buyerId);
        List<OrderResponseDto> dtos = orders.stream().map(OrderResponseDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }
}
