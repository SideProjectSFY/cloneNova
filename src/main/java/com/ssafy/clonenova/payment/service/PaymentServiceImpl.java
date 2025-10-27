package com.ssafy.clonenova.payment.service;

import com.ssafy.clonenova.payment.client.PortOneClient;
import com.ssafy.clonenova.payment.dto.PaymentRequest;
import com.ssafy.clonenova.payment.dto.PaymentResponse;
import com.ssafy.clonenova.payment.entity.Payment;
import com.ssafy.clonenova.payment.repository.PaymentRepository;
import com.ssafy.clonenova.common.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PortOneClient portOneClient;

    /**
     * [PF01] 결제 준비 (주문 생성)
     * - 서버가 merchant_uid(= pgTxId)를 발급하고 READY 상태로 저장
     * - 프론트가 결제창 호출에 필요한 pgTxId, amount 반환
     */
    @Override
    @Transactional
    public PaymentResponse.Prepare prepare(PaymentRequest.Prepare request) {

        String merchantUid = "MID-" + System.currentTimeMillis();

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .productId(request.getProductId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .pgTxId(merchantUid)          // 상점 주문번호
                .portPayId("IMP_PENDING")     // 결제 전 임시
                .status(PaymentStatus.READY)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        return PaymentResponse.Prepare.builder()
                .pgTxId(merchantUid)
                .amount(request.getAmount())
                .build();
    }

    /**
     * [PF02] 결제 완료 및 검증
     * - PortOne imp_uid로 결제 단건 조회
     * - merchant_uid(pgTxId)와 금액 교차 검증
     * - 상태/PG정보/imp_uid 반영 후 Completed 응답 반환
     */
    @Override
    @Transactional
    public PaymentResponse.Complete complete(PaymentRequest.Complete request) {

        // PortOne 조회 (신뢰 소스)
        Map<String, Object> po = portOneClient.getPaymentInfo(request.getPortPayId());

        String impUid = (String) po.get("imp_uid");              // PortOne 결제 고유 ID
        String merchantUid = (String) po.get("merchant_uid");    // 우리 상점 주문번호
        String pgTid = (String) po.get("pg_tid");
        String pgProvider = (String) po.get("pg_provider");
        String statusRaw = (String) po.get("status");
        Integer paidAmount = po.get("amount") == null
                ? null
                : ((Number) po.get("amount")).intValue();

        // PortOne paid_at이 epoch seconds라면 변환 (없으면 null)
        LocalDateTime paidAt = null;
        Object paidAtRaw = po.get("paid_at");
        if (paidAtRaw instanceof Number) {
            long epochSec = ((Number) paidAtRaw).longValue();
            paidAt = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(epochSec),
                    ZoneId.of("Asia/Seoul")
            );
        }

        // 우리 DB 결제 찾기 (merchant_uid = pgTxId)
        Payment payment = paymentRepository.findByPgTxId(merchantUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 merchant_uid의 결제가 존재하지 않습니다."));

        // 금액 검증
        if (paidAmount == null || !payment.getAmount().equals(paidAmount)) {
            throw new IllegalStateException("결제 금액 불일치");
        }

        // 상태 매핑 및 반영
        PaymentStatus mapped = PaymentStatus.from(statusRaw);
        payment.markStatus(
                mapped,
                pgProvider,
                pgTid,
                request.getPortPayId()        // imp_uid를 우리 필드명인 portPayId로 보관
        );

        // 응답 DTO 구성 (네 스펙에 맞춰 필드 채움)
        return PaymentResponse.Complete.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .productId(payment.getProductId())
                .userId(payment.getUserId())
                .portPayId(impUid)            // 응답에는 우리 필드명(portPayId)로 내려줌
                .errorCode(payment.getErrorCode())
                .errorMessage(payment.getErrorMessage())
                .pgProvider(pgProvider)
                .pgTxId(pgTid)                // PG 거래 ID
                .status(mapped)
                .amount(paidAmount)
                .paidAt(paidAt)
                .createdAt(payment.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * [PF11] 결제 실패 리포트 (선택)
     * - pgTxId 기준으로 찾되 없으면 신규 실패 레코드 생성
     */
//    @Override
//    @Transactional
//    public void reportFailure(PaymentRequest.FailureReport request) {
//
//        Payment payment = paymentRepository.findByPgTxId(request.getPgTxId())
//                .orElseGet(() ->
//                        Payment.builder()
//                                .orderId(null)
//                                .productId(request.getProductId())
//                                .userId(request.getUserId())
//                                .amount(0)
//                                .pgTxId(request.getPgTxId())
//                                .portPayId("IMP_UNKNOWN")
//                                .status(PaymentStatus.FAILED)
//                                .createdAt(LocalDateTime.now())
//                                .build()
//                );
//
//        payment.fail(
//                request.getErrorCode(),
//                request.getErrorMessage()
//        );
//    }
}
