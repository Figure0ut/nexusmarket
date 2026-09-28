package application.infrastructure.adapters.out.memory;

import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.ReturnRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
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
}
