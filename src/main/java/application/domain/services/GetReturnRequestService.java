package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.ReturnRepositoryPort;

/**
 * Atomic domain service responsible exclusively for querying a return request by its identifier.
 */
public class GetReturnRequestService {

    private final ReturnRepositoryPort returnRepositoryPort;

    public GetReturnRequestService(ReturnRepositoryPort returnRepositoryPort) {
        if (returnRepositoryPort == null) {
            throw new IllegalArgumentException("ReturnRepositoryPort cannot be null.");
        }
        this.returnRepositoryPort = returnRepositoryPort;
    }

    public ReturnRequest getReturnRequest(String returnId) {
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty.");
        }
        return returnRepositoryPort.findReturnById(returnId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Return request not found with ID: " + returnId));
    }
}
