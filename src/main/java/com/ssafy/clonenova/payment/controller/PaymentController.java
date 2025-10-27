package com.ssafy.clonenova.payment.controller;

import com.ssafy.clonenova.payment.dto.PaymentRequest;
import com.ssafy.clonenova.payment.dto.PaymentResponse;
import com.ssafy.clonenova.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/prepare")
    public PaymentResponse.Prepare prepare(@RequestBody PaymentRequest.Prepare req) {
        return paymentService.prepare(req);
    }

    @PostMapping("/complete")
    public PaymentResponse.Complete complete(@RequestBody PaymentRequest.Complete req) {
        return paymentService.complete(req);
    }
}
