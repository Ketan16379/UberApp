package com.springBoot.project.uber.uberApp.entities;

import com.springBoot.project.uber.uberApp.entities.enums.TransactionMethod;
import com.springBoot.project.uber.uberApp.entities.enums.TransactionType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private TransactionType transactionType;

    private TransactionMethod transactionMethod;

    @OneToOne
    private Ride ride;

    @ManyToOne
    private Wallet wallet;

    private String transactionId;

    @CreationTimestamp
    private LocalDateTime timeStamp;
}
