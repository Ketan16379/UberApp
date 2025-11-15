package com.springBoot.project.uber.uberApp.services.impl;

import com.springBoot.project.uber.uberApp.services.DistanceService;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
public class DistanceServiceOSRMImpl implements DistanceService {

    @Override
    public double calculateDistance(Point scr, Point dest) {
        //TODO CALL THE THIRD PARTY API OSRM TO FETCH THE DISTANCE
        return 0;
    }
}
