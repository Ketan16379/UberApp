package com.springBoot.project.uber.uberApp.dto;

import com.springBoot.project.uber.uberApp.entities.Rider;
import com.springBoot.project.uber.uberApp.entities.enums.PaymentMethod;
import com.springBoot.project.uber.uberApp.entities.enums.RideRequestStatus;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

public class RideDto {

    private Long id;

    private Point pickUpLocation;

    private Point dropOffLocation;

    private LocalDateTime createdTime;

    private RiderDto rider;

    private DriverDto driver;

    private PaymentMethod paymentMethod;

    private String otp;

    private RideRequestStatus rideRequestStatus;

    private Double fare;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;
}
