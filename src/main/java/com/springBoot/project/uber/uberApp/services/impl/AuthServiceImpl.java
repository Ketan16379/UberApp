package com.springBoot.project.uber.uberApp.services.impl;

import com.springBoot.project.uber.uberApp.dto.DriverDto;
import com.springBoot.project.uber.uberApp.dto.SignUpDto;
import com.springBoot.project.uber.uberApp.dto.UserDto;
import com.springBoot.project.uber.uberApp.services.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public String login(String email, String password) {
        return "";
    }

    @Override
    public UserDto signup(SignUpDto signUpDto) {
        return null;
    }

    @Override
    public DriverDto onboardNewDriver(Long userId) {
        return null;
    }
}
