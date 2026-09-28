package application.infrastructure.adapters.out.memory;

import application.domain.ports.out.PaymentGatewayPort;
import application.domain.valueobjects.Money;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPaymentGatewayAdapter implements PaymentGatewayPort {

    @Override
    public boolean processPayment(String orderId, Money amount, String paymentToken) {
        return paymentToken != null && !paymentToken.trim().isEmpty();
    }

    @Override
    public boolean processRefund(String refundId, Money amount) {
        return refundId != null && !refundId.trim().isEmpty();
    }
}
