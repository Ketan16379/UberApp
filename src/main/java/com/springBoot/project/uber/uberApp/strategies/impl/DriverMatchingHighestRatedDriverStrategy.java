package com.springBoot.project.uber.uberApp.strategies.impl;

import com.springBoot.project.uber.uberApp.dto.RideRequestDto;
import com.springBoot.project.uber.uberApp.entities.Driver;
import com.springBoot.project.uber.uberApp.entities.RideRequest;
import com.springBoot.project.uber.uberApp.strategies.DriverMatchingStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverMatchingHighestRatedDriverStrategy implements DriverMatchingStrategy{

    @Override
    public List<Driver> findMatchingDrivers(RideRequest rideRequestDto) {
        return List.of();
    }
}
