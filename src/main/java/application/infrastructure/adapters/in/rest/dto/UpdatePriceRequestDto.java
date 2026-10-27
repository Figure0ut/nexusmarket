package application.infrastructure.adapters.in.rest.dto;

public class UpdatePriceRequestDto {
    private double newPrice;

    public UpdatePriceRequestDto() {}

    public double getNewPrice() { return newPrice; }
    public void setNewPrice(double newPrice) { this.newPrice = newPrice; }
}
