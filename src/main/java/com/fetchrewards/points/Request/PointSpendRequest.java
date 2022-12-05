package com.fetchrewards.points.Request;

import lombok.Data;

@Data
public class PointSpendRequest {

    private long userId;

    private long points;
}
