package com.fetchrewards.points.Utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.fetchrewards.points.Exception.TransactionException;
import com.fetchrewards.points.Model.Transaction;
import com.fetchrewards.points.Model.User;
import com.fetchrewards.points.Request.AddPointRequest;
import com.fetchrewards.points.Request.PointSpendRequest;
import com.fetchrewards.points.Response.AddPointResponse;
import com.fetchrewards.points.Response.PointSpendResponse;

@SpringBootTest
public class UserPointServiceTest {

    @InjectMocks
    UserPointService service;

    @Mock
    User user;

    Map<Long, User> userMap = new HashMap<>();

    @BeforeEach
    public void setup() {
        Map<String, Long> pointPerPayer = new HashMap<>();
        pointPerPayer.put("A", 0L);

        Transaction transaction1 = new Transaction();
        transaction1.setPayer("A");
        transaction1.setPoints(0L);
        transaction1.setTransactionId(UUID.randomUUID());
        transaction1.setTimestamp(LocalDateTime.now());

        user.setTotalRewardPoints(0);
        user.setPointsPerPayer(pointPerPayer);
        user.setUserId(1);
        userMap.put(user.getUserId(), user);
    }

    @Test
    public void testFetchAllUsersTransactions() {
        
        AddPointRequest request = new AddPointRequest();
        request.setPayer("B");
        request.setPoint(5);

        AddPointResponse response = service.addUserPoints(user.getUserId(), request);
        assertNotNull(response);
        assertEquals(response.getPoints(), 5);
        
        Map<Long, PriorityQueue<Transaction>> usersTransactions = service.fetchAllUsersTransactions();
        assertEquals(usersTransactions.entrySet().size(), 1);

        long resultUserId = usersTransactions.keySet().iterator().next();

        assertEquals(user.getUserId(), resultUserId);
    }

    @Test
    public void testFetchTransactionById() {
        
        AddPointRequest request = new AddPointRequest();
        request.setPayer("B");
        request.setPoint(5);

        AddPointResponse addPoint = service.addUserPoints(user.getUserId(), request);
        assertNotNull(addPoint);
        assertEquals(addPoint.getPoints(), 5);
        
        PriorityQueue<Transaction> response = service.fetchTransactionById(user.getUserId());

        Transaction transaction = response.poll();

        assertEquals("B", transaction.getPayer());
    }

    @Test
    public void testSpendPoint() {
        
        AddPointRequest addPointRequest = new AddPointRequest();
        addPointRequest.setPayer("B");
        addPointRequest.setPoint(5);

        AddPointResponse addPointResponse = service.addUserPoints(user.getUserId(), addPointRequest);
        assertNotNull(addPointResponse);
        assertEquals(addPointResponse.getPoints(), 5);
        
        PointSpendRequest request = new PointSpendRequest();
        request.setPoints(2);
        request.setUserId(user.getUserId());
        List<PointSpendResponse> response = service.spendPoints(request);
        assertEquals(response.size(), 1);
        assertEquals("-2", response.get(0).getPoints());
        assertEquals("B", response.get(0).getPayer());
    }

    @Test
    public void testSpendPointThrowsTransactionException() {
        
        AddPointRequest addPointRequest = new AddPointRequest();
        addPointRequest.setPayer("B");
        addPointRequest.setPoint(5);

        AddPointResponse addPointResponse = service.addUserPoints(user.getUserId(), addPointRequest);
        assertNotNull(addPointResponse);
        assertEquals(addPointResponse.getPoints(), 5);
        
        PointSpendRequest request = new PointSpendRequest();
        request.setPoints(100);
        request.setUserId(user.getUserId());
        assertThrows(TransactionException.class, () -> service.spendPoints(request));
    }
    
}
