package com.ssafy.clonenova.payment.entity;

import com.ssafy.clonenova.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long productId;

    @Column(length = 36, nullable = false)
    private String userId;

    @Column(length = 100, nullable = false)
    private String portPayId; // imp_uid (PortOne 결제 고유 ID)

    @Column(length = 64)
    private String errorCode;

    @Column(length = 300)
    private String errorMessage;

    @Column(length = 60)
    private String pgProvider;

    @Column(length = 120, nullable = false)
    private String pgTxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // READY, PAID, FAILED, CANCELLED, UNKNOWN


    @Column(nullable = false)
    private Integer amount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public static Payment createReady(Long orderId, Long productId, String userId,
                                      Integer amount, String merchantUid) {
        Payment p = new Payment();
        p.orderId = orderId;
        p.productId = productId;
        p.userId = userId;
        p.amount = amount;
        p.pgTxId = merchantUid;      
        p.portPayId = "IMP_PENDING"; //결제 전 임시
        p.status = PaymentStatus.READY;
        return p;
    }

    /** 상태 업데이트 (검증 후) */
    public void markStatus(PaymentStatus status, String pgProvider, String pgTxId, String impUid) {
        this.status = status;
        this.pgProvider = pgProvider;
        this.pgTxId = pgTxId;   // PG 거래번호로 교체/추가
        this.portPayId = impUid;
    }
    
    public void fail(String code, String message) {
        this.status = PaymentStatus.FAILED;
        this.errorCode = code;
        this.errorMessage = message;
    }

}
