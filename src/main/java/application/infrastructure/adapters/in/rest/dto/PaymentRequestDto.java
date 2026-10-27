package application.infrastructure.adapters.in.rest.dto;

public class PaymentRequestDto {
    private String paymentToken;
    private String buyerEmail;

    public PaymentRequestDto() {}

    public String getPaymentToken() { return paymentToken; }
    public void setPaymentToken(String paymentToken) { this.paymentToken = paymentToken; }

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
}
