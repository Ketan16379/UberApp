package com.springBoot.project.uber.uberApp.services;

import com.springBoot.project.uber.uberApp.dto.DriverDto;
import com.springBoot.project.uber.uberApp.dto.SignUpDto;
import com.springBoot.project.uber.uberApp.dto.UserDto;

public interface AuthService {

    String  login(String email, String password);

    UserDto signup(SignUpDto signUpDto);

    DriverDto onboardNewDriver(Long userId);
}
