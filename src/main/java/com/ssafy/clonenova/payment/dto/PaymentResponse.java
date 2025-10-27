package com.ssafy.clonenova.payment.dto;

import lombok.*;
import java.time.LocalDateTime;

import com.ssafy.clonenova.common.enums.PaymentStatus;

@Getter 
@Builder
public class PaymentResponse {

    /** [PF01] 결제 준비 응답 */
	@Getter 
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
    public static class Prepare {
        private Long orderId;
        private Long productId;
        private String userId;
        private Integer amount;
        private String pgTxId; 
        private String portPayId;
        private LocalDateTime createdAt;
    }

    /** [PF02] 결제 완료 */
	@Getter 
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
    public static class Complete {
        private Long id;
        private Long orderId;
        private Long productId;
        private String userId;
        private String portPayId;
        private String errorCode;
        private String errorMessage;
        private String pgProvider;
        private String pgTxId;
        private PaymentStatus status;      // READY,PAID,FAILED,CANCELLED
        private Integer amount;     // 실 결제 금액
        private LocalDateTime paidAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
