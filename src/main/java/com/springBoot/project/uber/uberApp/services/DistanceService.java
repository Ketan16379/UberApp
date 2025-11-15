package com.springBoot.project.uber.uberApp.services;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;


public interface DistanceService {

    double calculateDistance(Point scr, Point dest);

}
