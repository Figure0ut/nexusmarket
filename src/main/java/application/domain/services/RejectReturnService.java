package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.ReturnRepositoryPort;

/**
 * Atomic domain service responsible exclusively for rejecting a return request.
 */
public class RejectReturnService {

    private final ReturnRepositoryPort returnRepositoryPort;

    public RejectReturnService(ReturnRepositoryPort returnRepositoryPort) {
        if (returnRepositoryPort == null) {
            throw new IllegalArgumentException("ReturnRepositoryPort cannot be null.");
        }
        this.returnRepositoryPort = returnRepositoryPort;
    }

    public void rejectReturn(String returnId) {
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty.");
        }
        ReturnRequest request = returnRepositoryPort.findReturnById(returnId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Return request not found with ID: " + returnId));
        request.reject();
        returnRepositoryPort.saveReturn(request);
    }
}
