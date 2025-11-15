package com.springBoot.project.uber.uberApp.strategies.impl;

import com.springBoot.project.uber.uberApp.dto.RideRequestDto;
import com.springBoot.project.uber.uberApp.entities.RideRequest;
import com.springBoot.project.uber.uberApp.services.DistanceService;
import com.springBoot.project.uber.uberApp.strategies.RideFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class RideFareSurgePricingFareCalculationStrategy implements RideFareCalculationStrategy {

    private final DistanceService distanceService;

    @Override
    public double calculateFare(RideRequest rideRequest) {
        return 0;
    }
}
