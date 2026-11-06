package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.ReturnReason;

public record ReturnRequestDto(String returnId, String orderId, String productId, ReturnReason reason) {
}
