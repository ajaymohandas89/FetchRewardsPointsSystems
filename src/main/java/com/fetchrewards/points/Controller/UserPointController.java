package com.fetchrewards.points.Controller;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fetchrewards.points.Model.Transaction;
import com.fetchrewards.points.Request.AddPointRequest;
import com.fetchrewards.points.Request.PointSpendRequest;
import com.fetchrewards.points.Response.AddPointResponse;
import com.fetchrewards.points.Response.PointSpendResponse;
import com.fetchrewards.points.Utility.UserPointService;

@RestController
@RequestMapping("/userpoints")
public class UserPointController {

    @Autowired
    UserPointService service;

    /** 
     * Fetch all transactions of all users in the systems
    */
    @GetMapping("/v1/transactions")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<Long, PriorityQueue<Transaction>>> fetchAllUsersTransactions() {

        return ResponseEntity.ok().body(service.fetchAllUsersTransactions());
    }

    @GetMapping("/v1/transactions/{id}")
    public ResponseEntity<PriorityQueue<Transaction>> fetchTransactionById(@Validated @PathVariable("id") long id) {
        
        return ResponseEntity.ok().body(service.fetchTransactionById(id));
    }

    @GetMapping("/v1/balance/{id}")
    public ResponseEntity<List<StringBuilder>> fetchUserBalance(@Validated @PathVariable long id) {

        return ResponseEntity.ok().body(service.fetchUserBalance(id));
    }
    
    @PostMapping("/v1/addPoints/{id}")
    public ResponseEntity<AddPointResponse> addUserPoints(@Validated @PathVariable("id") long id, 
                                                    @Validated @RequestBody AddPointRequest request) {
        return ResponseEntity.ok().body(service.addUserPoints(id, request));
    }

    @PutMapping("/v1/spend")
    public ResponseEntity<List<PointSpendResponse>> spendPoints(@Validated @RequestBody PointSpendRequest request) {
        
        return ResponseEntity.ok().body(service.spendPoints(request));
    }
}
