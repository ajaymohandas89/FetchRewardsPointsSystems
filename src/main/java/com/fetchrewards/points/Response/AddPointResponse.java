package com.fetchrewards.points.Response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AddPointResponse {
    
    private String payer;

    private long points;

    private LocalDateTime timestamp; 
}
