package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.ReturnRepositoryPort;

/**
 * Atomic domain service responsible exclusively for approving a return request.
 */
public class ApproveReturnService {

    private final ReturnRepositoryPort returnRepositoryPort;

    public ApproveReturnService(ReturnRepositoryPort returnRepositoryPort) {
        if (returnRepositoryPort == null) {
            throw new IllegalArgumentException("ReturnRepositoryPort cannot be null.");
        }
        this.returnRepositoryPort = returnRepositoryPort;
    }

    public void approveReturn(String returnId) {
        if (returnId == null || returnId.trim().isEmpty()) {
            throw new IllegalArgumentException("Return ID cannot be null or empty.");
        }
        ReturnRequest request = returnRepositoryPort.findReturnById(returnId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Return request not found with ID: " + returnId));
        request.approve();
        returnRepositoryPort.saveReturn(request);
    }
}
