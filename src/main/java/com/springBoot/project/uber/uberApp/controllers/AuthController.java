package com.springBoot.project.uber.uberApp.controllers;

import com.springBoot.project.uber.uberApp.dto.*;
import com.springBoot.project.uber.uberApp.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    ResponseEntity<UserDto> signUp(@RequestBody SignupDto signupDto) {
        return new ResponseEntity<>(authService.signup(signupDto), HttpStatus.CREATED);
    }

    @PostMapping("/onBoardNewDriver")
    ResponseEntity<DriverDto> onBoardNewDriver(@PathVariable Long userId, @RequestBody OnBoardDriverDTO onBoardDriverDTO){
        return new ResponseEntity<>(authService.onboardNewDriver(userId,
                onBoardDriverDTO.getVehicleId()), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        String tokens[] = authService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        return ResponseEntity.ok(new LoginResponseDTO(tokens[0]));
    }
}

