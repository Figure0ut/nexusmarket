package application.domain.ports.out;

import application.domain.valueobjects.Money;

public interface PaymentGatewayPort {
    boolean processPayment(String orderId, Money amount, String paymentToken);
    boolean processRefund(String refundId, Money amount);
}
