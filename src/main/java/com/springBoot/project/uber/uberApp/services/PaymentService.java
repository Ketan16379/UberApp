package com.springBoot.project.uber.uberApp.services;

import com.springBoot.project.uber.uberApp.entities.Payment;
import com.springBoot.project.uber.uberApp.entities.Ride;

public interface PaymentService {

    void processPayment(Payment payment);
    Payment createNewPayment(Ride ride);
}
