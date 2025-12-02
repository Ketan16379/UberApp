package com.springBoot.project.uber.uberApp.services.impl;

import com.springBoot.project.uber.uberApp.dto.DriverDto;
import com.springBoot.project.uber.uberApp.dto.SignUpDto;
import com.springBoot.project.uber.uberApp.dto.UserDto;
import com.springBoot.project.uber.uberApp.entities.Rider;
import com.springBoot.project.uber.uberApp.entities.User;
import com.springBoot.project.uber.uberApp.entities.enums.Role;
import com.springBoot.project.uber.uberApp.exceptions.RuntimeConflictException;
import com.springBoot.project.uber.uberApp.repositories.UserRepository;
import com.springBoot.project.uber.uberApp.services.AuthService;
import com.springBoot.project.uber.uberApp.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {

    private final RiderService riderService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public String login(String email, String password) {
        return "";
    }

    @Override
    public UserDto signup(SignUpDto signUpDto) {

        User user = userRepository.findByEmail(signUpDto.getEmail()).orElse(null);

        if(user != null){
            throw new RuntimeConflictException("Cannot signup, User already exists with email " + signUpDto.getEmail());
        }
        User mappedUser = modelMapper.map(signUpDto, User.class);
        user.setRoles(Set.of(Role.RIDER));
        User savedUser = userRepository.save(mappedUser);

        //CREATE USER RELEATED ENTITIES

        riderService.createNewRider(savedUser);
        //TODO ADD WALLET RELATED SERVICE HERE
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardNewDriver(Long userId) {
        return null;
    }
}
