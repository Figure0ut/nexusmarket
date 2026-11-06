package application.infrastructure.adapters.in.rest.dto;

/**
 * reusable = true: the returned units go back to available stock; false: they are stored as damaged stock.
 */
public record ProcessRefundRequestDto(String refundId, String returnId, boolean reusable) {
}
