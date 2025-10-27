package com.ssafy.clonenova.payment.dto;

import lombok.*;

@Getter 
@Builder
public class PaymentRequest {

    /** [PF01] 결제 준비 */
	@Getter 
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
    public static class Prepare {
        private Long orderId;
        private Long productId;
        private String userId;
        private Integer amount;
    }

    /** [PF02] 결제 완료 검증 */
	@Getter 
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
    public static class Complete {
        private Long orderId;
        private Long productId;
        private String userId;
        private String portPayId; // PortOne 결제 ID
        private String pgTxId;    // PG ID
        private Integer amount;     
    }

    /** [PF11] 결제 실패  */
	@Getter 
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
    public static class FailureReport {
        private Long id;
        private Long productId;
        private String userId;
        private String errorCode;
        private String errorMessage;
        private String pgTxId;
    }
}
