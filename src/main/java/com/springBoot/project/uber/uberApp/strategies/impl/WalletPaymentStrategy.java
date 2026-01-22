package com.springBoot.project.uber.uberApp.strategies.impl;


import com.springBoot.project.uber.uberApp.entities.Driver;
import com.springBoot.project.uber.uberApp.entities.Payment;
import com.springBoot.project.uber.uberApp.entities.Rider;
import com.springBoot.project.uber.uberApp.entities.enums.TransactionMethod;
import com.springBoot.project.uber.uberApp.services.WalletService;
import com.springBoot.project.uber.uberApp.strategies.PaymentStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//RIDER HAD 232, DRIVER HAD 500
//RIDE COST IS 100, COMMISSION = 30
//RIDER --> 232 - 100
//DERIVER --> 500 * (100 -30) = 570

@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;

    @Override
    @Transactional
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();
        Rider rider = payment.getRide().getRider();

        walletService.deductMoneyFromWallet(rider.getUser(), payment.getAmount()
                ,null, payment.getRide(), TransactionMethod.RIDE);

        double driversCut = payment.getAmount() * (1 - PLATFORM_COMMISSION);

        walletService.addMoneyToWallet(driver.getUser(), driversCut
                ,null, payment.getRide(), TransactionMethod.RIDE);

    }
}
