package application.infrastructure.adapters.out.memory;

import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.ReturnRepositoryPort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory test double of the output port (used only by unit tests).
 */
public class InMemoryReturnRepositoryAdapter implements ReturnRepositoryPort {

    private final Map<String, ReturnRequest> returnStorage = new ConcurrentHashMap<>();
    private final Map<String, Refund> refundStorage = new ConcurrentHashMap<>();

    @Override
    public Optional<ReturnRequest> findReturnById(String returnId) {
        return Optional.ofNullable(returnStorage.get(returnId));
    }

    @Override
    public ReturnRequest saveReturn(ReturnRequest returnRequest) {
        returnStorage.put(returnRequest.getReturnId(), returnRequest);
        return returnRequest;
    }

    @Override
    public Optional<Refund> findRefundById(String refundId) {
        return Optional.ofNullable(refundStorage.get(refundId));
    }

    @Override
    public Refund saveRefund(Refund refund) {
        refundStorage.put(refund.getRefundId(), refund);
        return refund;
    }

    @Override
    public List<ReturnRequest> findAllReturns() {
        return List.copyOf(returnStorage.values());
    }

    @Override
    public List<Refund> findAllRefunds() {
        return List.copyOf(refundStorage.values());
    }
}
