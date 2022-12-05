package com.fetchrewards.points.Request;

import lombok.Data;

@Data
public class AddPointRequest {

    private String payer;
    private long point;
    
}
