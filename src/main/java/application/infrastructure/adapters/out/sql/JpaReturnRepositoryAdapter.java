package application.infrastructure.adapters.out.sql;

import application.domain.models.Refund;
import application.domain.models.ReturnRequest;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.valueobjects.Money;
import application.infrastructure.adapters.out.sql.entity.RefundEntity;
import application.infrastructure.adapters.out.sql.entity.ReturnRequestEntity;
import application.infrastructure.adapters.out.sql.repository.SpringDataJpaRefundRepository;
import application.infrastructure.adapters.out.sql.repository.SpringDataJpaReturnRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Primary
public class JpaReturnRepositoryAdapter implements ReturnRepositoryPort {

    private final SpringDataJpaReturnRepository returnRepository;
    private final SpringDataJpaRefundRepository refundRepository;

    public JpaReturnRepositoryAdapter(SpringDataJpaReturnRepository returnRepository,
                                      SpringDataJpaRefundRepository refundRepository) {
        this.returnRepository = returnRepository;
        this.refundRepository = refundRepository;
    }

    @Override
    public Optional<ReturnRequest> findReturnById(String returnId) {
        return returnRepository.findById(returnId).map(this::toDomain);
    }

    @Override
    public ReturnRequest saveReturn(ReturnRequest returnRequest) {
        ReturnRequestEntity entity = toEntity(returnRequest);
        ReturnRequestEntity saved = returnRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Refund> findRefundById(String refundId) {
        return refundRepository.findById(refundId).map(this::toDomain);
    }

    @Override
    public Refund saveRefund(Refund refund) {
        RefundEntity entity = toEntity(refund);
        RefundEntity saved = refundRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<ReturnRequest> findAllReturns() {
        return returnRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Refund> findAllRefunds() {
        return refundRepository.findAll().stream().map(this::toDomain).toList();
    }

    private ReturnRequest toDomain(ReturnRequestEntity entity) {
        return new ReturnRequest(
                entity.getReturnId(),
                entity.getOrderId(),
                entity.getBuyerId(),
                entity.getProductId(),
                entity.getReason(),
                entity.getStatus()
        );
    }

    private ReturnRequestEntity toEntity(ReturnRequest returnRequest) {
        ReturnRequestEntity entity = new ReturnRequestEntity();
        entity.setReturnId(returnRequest.getReturnId());
        entity.setOrderId(returnRequest.getOrderId());
        entity.setBuyerId(returnRequest.getBuyerId());
        entity.setProductId(returnRequest.getProductId());
        entity.setReason(returnRequest.getReason());
        entity.setStatus(returnRequest.getStatus());
        return entity;
    }

    private Refund toDomain(RefundEntity entity) {
        return new Refund(
                entity.getRefundId(),
                entity.getReturnId(),
                entity.getBuyerId(),
                new Money(entity.getAmount()),
                entity.getStatus()
        );
    }

    private RefundEntity toEntity(Refund refund) {
        RefundEntity entity = new RefundEntity();
        entity.setRefundId(refund.getRefundId());
        entity.setReturnId(refund.getReturnId());
        entity.setBuyerId(refund.getBuyerId());
        entity.setAmount(refund.getAmount() != null ? refund.getAmount().getAmount() : java.math.BigDecimal.ZERO);
        entity.setStatus(refund.getStatus());
        return entity;
    }
}
