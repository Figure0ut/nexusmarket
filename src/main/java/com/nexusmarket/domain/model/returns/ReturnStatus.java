package com.nexusmarket.domain.model.returns;

/**
 * Value Object representing return request processing lifecycle state.
 */
public enum ReturnStatus {
    REQUESTED,
    APPROVED,
    REJECTED,
    ITEM_RECEIVED
}
