package com.springBoot.project.uber.uberApp.repositories;

import com.springBoot.project.uber.uberApp.entities.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

//ST_DistanceE(point1, point2)
//ST_DWithin(point1, point2)

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d.*, ST_Distance(d.current_location, :pickupLocation) FROM drivers as d where available = true AND ST_DWithin()")
    List<Driver> findMatchingDrivers(Point pickUpLocation);
}
