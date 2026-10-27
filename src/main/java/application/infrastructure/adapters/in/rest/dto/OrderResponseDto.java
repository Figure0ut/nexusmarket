package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.OrderStatus;
import application.domain.models.Order;
import application.domain.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderResponseDto {
    private String orderId;
    private String buyerId;
    private OrderStatus status;
    private double totalAmount;
    private String shippingAddress;
    private List<OrderItemResponseDto> items = new ArrayList<>();

    public OrderResponseDto() {}

    public static OrderResponseDto fromDomain(Order order) {
        if (order == null) return null;
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setBuyerId(order.getBuyerId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount().getAmount().doubleValue() : 0.0);
        dto.setShippingAddress(order.getShippingAddress() != null ? order.getShippingAddress().getStreet() : null);

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                dto.getItems().add(new OrderItemResponseDto(
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice() != null ? item.getUnitPrice().getAmount().doubleValue() : 0.0,
                        item.getQuantity(),
                        item.calculateSubtotal() != null ? item.calculateSubtotal().getAmount().doubleValue() : 0.0
                ));
            }
        }
        return dto;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public List<OrderItemResponseDto> getItems() { return items; }
    public void setItems(List<OrderItemResponseDto> items) { this.items = items; }
}
