package application.infrastructure.adapters.in.rest.dto;

public record StockOperationRequestDto(String productId, String warehouseId, int quantity) {
}
