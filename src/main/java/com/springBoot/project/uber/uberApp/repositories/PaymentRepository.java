package com.springBoot.project.uber.uberApp.repositories;

import com.springBoot.project.uber.uberApp.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
