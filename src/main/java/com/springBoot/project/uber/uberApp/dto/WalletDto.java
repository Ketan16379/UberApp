package com.springBoot.project.uber.uberApp.dto;

import com.springBoot.project.uber.uberApp.entities.WalletTransaction;
import lombok.Data;

import java.util.List;

@Data
public class WalletDto {

    private Long id;

    private UserDto userDto;

    private Double balance;

    private List<WalletTransactionDto> transactions;
}
