package com.fetchrewards.points.Model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import lombok.Data;

@Data
public class User {

    private long userId;

    private long totalRewardPoints;

    private Map<String, Long> pointsPerPayer;

    private PriorityQueue<Transaction> transactionsQueue;

    public User(long userId) {
        this.userId = userId;
        this.totalRewardPoints = 0;
        this.pointsPerPayer = new HashMap<>();
        this.transactionsQueue = new PriorityQueue<>(new Comparator<Transaction>() {
            @Override
            public int compare(Transaction obj1 , Transaction obj2) {
                return obj1.getTimestamp().compareTo(obj2.getTimestamp());
            }
            
        });
    }

}
