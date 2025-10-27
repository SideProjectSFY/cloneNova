package com.ssafy.clonenova.payment.service;

import com.ssafy.clonenova.payment.dto.PaymentRequest;
import com.ssafy.clonenova.payment.dto.PaymentResponse;

public interface PaymentService {
    // [PF01] 결제 준비
    PaymentResponse.Prepare prepare(PaymentRequest.Prepare request);

    // [PF02] 결제 완료 및 검증 & 재화 지급
    PaymentResponse.Complete complete(PaymentRequest.Complete request);
}
