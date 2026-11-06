package application.domain.ports.out;

import application.domain.models.Refund;
import application.domain.models.ReturnRequest;

import java.util.List;
import java.util.Optional;

public interface ReturnRepositoryPort {
    Optional<ReturnRequest> findReturnById(String returnId);
    ReturnRequest saveReturn(ReturnRequest returnRequest);
    Optional<Refund> findRefundById(String refundId);
    Refund saveRefund(Refund refund);
    List<ReturnRequest> findAllReturns();
    List<Refund> findAllRefunds();
}
