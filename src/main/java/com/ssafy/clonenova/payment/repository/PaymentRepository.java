package com.ssafy.clonenova.payment.repository;

import com.ssafy.clonenova.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPgTxId(String pgTxId);
    Optional<Payment> findByPortPayId(String portPayId);
}
