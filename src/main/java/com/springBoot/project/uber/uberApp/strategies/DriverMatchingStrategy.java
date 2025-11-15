package com.springBoot.project.uber.uberApp.strategies;

import com.springBoot.project.uber.uberApp.dto.RideRequestDto;
import com.springBoot.project.uber.uberApp.entities.Driver;
import com.springBoot.project.uber.uberApp.entities.RideRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DriverMatchingStrategy {
    List<Driver> findMatchingDrivers(RideRequest rideRequest);
}
