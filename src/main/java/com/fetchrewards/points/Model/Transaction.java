package com.fetchrewards.points.Model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class Transaction {
    private UUID transactionId;

    private String payer;

    private long points;

    private LocalDateTime timestamp;

}
