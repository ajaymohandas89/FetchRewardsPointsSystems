package com.fetchrewards.points.Utility;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fetchrewards.points.Exception.TransactionException;
import com.fetchrewards.points.Exception.UserNotFoundException;
import com.fetchrewards.points.Model.Transaction;
import com.fetchrewards.points.Model.User;
import com.fetchrewards.points.Request.AddPointRequest;
import com.fetchrewards.points.Request.PointSpendRequest;
import com.fetchrewards.points.Response.AddPointResponse;
import com.fetchrewards.points.Response.PointSpendResponse;

@Service
public class UserPointService {
    
    Logger logger = LoggerFactory.getLogger(UserPointService.class);

    private static final Map<Long, User> userMap = new HashMap<>();

    public Map<Long, PriorityQueue<Transaction>> fetchAllUsersTransactions() {

        logger.info("fetch all Users transactions");
        
        Map<Long, PriorityQueue<Transaction>> usersTransactions = new HashMap<>();
        userMap.entrySet()
        .forEach(
            (user) -> usersTransactions.put(user.getKey(),user.getValue().getTransactionsQueue()));
        
        return usersTransactions;
    }

    public PriorityQueue<Transaction> fetchTransactionById(long id) {

        logger.info("fetch User transactions by user id " + id);
        if(userMap.containsKey(id)) {
            PriorityQueue<Transaction> userTransaction = userMap.get(id).getTransactionsQueue();
            return userTransaction;
        } else {
            throw new UserNotFoundException("Exception fetching transaction for user " + id +  " found");
        }
    }

    public List<StringBuilder> fetchUserBalance(long id) {
        List<StringBuilder> userBalanceList = new ArrayList<>();
        if (!userMap.containsKey(id)) {
            throw new UserNotFoundException("Existing fetching balance for user " + id +  " found");
        }

        logger.info("Building user balance for user " + id);

        Map<String, Long> pointsPerPayer = userMap.get(id).getPointsPerPayer();

        for(Map.Entry<String, Long> data: pointsPerPayer.entrySet()) {
            userBalanceList.add(new StringBuilder(data.getKey() + ":" + data.getValue()));
        }

        return userBalanceList;
    }

    /**
     * Adding points for a user
     * @param id long
     * @param request AddPointRequest
     * @return AddPointResponse
     */
    public AddPointResponse addUserPoints(long id, AddPointRequest request) {
        logger.info("Calling UserPointService to add user points");
        User user;
        Transaction transaction = new Transaction();
        transaction.setPayer(request.getPayer());
        transaction.setPoints(request.getPoint());
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTimestamp(LocalDateTime.now());
        
        if(!userMap.containsKey(id)) {
            user = new User(id);
            userMap.put(user.getUserId(), user);
        } else {
            user = userMap.get(id);
        }

        long transationPoint = transaction.getPoints();

        long totalUserRewardPoints = user.getTotalRewardPoints();

        Map<String, Long> pointsPerPayer = user.getPointsPerPayer();

        PriorityQueue<Transaction> transactionsQueue = user.getTransactionsQueue(); 

        /**
         * If new added points is positive simply add to the transaction queue
         * If new added points is negative then do the following
         * Check if the sum of current points and new added points and if positive simply add
         * if the sum of current points and new added points is negative throw error
         * if the sum of current points and new added points is 0 then remove the current transaction from queue
         */

        if (transationPoint > 0) {
            transactionsQueue.offer(transaction);
        } else {
            if (!pointsPerPayer.containsKey(transaction.getPayer())) {
                throw new TransactionException("Exception while adding points to new payer cannot have negative points");
            } else {
                Transaction existingTransaction = transactionsQueue.stream()
                                                        .filter(q -> q.getPayer().equals(transaction.getPayer()))
                                                        .findFirst()
                                                        .orElse(null);

                long existingPointsPerPayer = pointsPerPayer.get(transaction.getPayer());
                if(existingPointsPerPayer + transationPoint > 0) {
                    existingTransaction.setPoints(transationPoint + existingPointsPerPayer);
                } else if (existingPointsPerPayer + transationPoint == 0) {
                    transactionsQueue.remove(existingTransaction);
                } else {
                    throw new TransactionException("Exception while adding points to existing payer as it will lead to negative points");
                }
            }
        }
        logger.info("Successfully updated user pointsperpayer Map and transation queue");

        user.setTotalRewardPoints(totalUserRewardPoints + transationPoint);

        pointsPerPayer.put(transaction.getPayer() , (pointsPerPayer.getOrDefault(transaction.getPayer(), 0L).longValue()) + transationPoint);
        logger.info("pointsPerPayer " + pointsPerPayer);
        user.setPointsPerPayer(pointsPerPayer);

        AddPointResponse response = new AddPointResponse();
        response.setPayer(transaction.getPayer());
        response.setPoints(transaction.getPoints());
        response.setTimestamp(transaction.getTimestamp());

        return response;
    }

    public List<PointSpendResponse> spendPoints(PointSpendRequest request) {
        if (!userMap.containsKey(request.getUserId())) {
            throw new UserNotFoundException("Exception while spending point, user not found");
        }

        logger.info("Calling spendPoints api in UserPointService ");
        
        List<PointSpendResponse> spendPointsPerPayerList = new ArrayList<>();
        long spendPoint = request.getPoints();

        User user = userMap.get(request.getUserId());
        Map<String, Long> pointsPerPayer = user.getPointsPerPayer();
        PriorityQueue<Transaction> transactionsQ = user.getTransactionsQueue();
        long totalRewardPoints = user.getTotalRewardPoints();

        if(spendPoint > totalRewardPoints) {
            throw new TransactionException("Exception at calling spend api as point spent exceeds total points");
        }
        List<Transaction> userTransactions = new ArrayList<>();

        /*
         * If points to be spent is less than current transaction points spend all and update transation queue
         * If points to be spent is greater than current transation points then spend all points of current transaction queue and update
         */

        while(spendPoint > 0 && !transactionsQ.isEmpty()) {
            long remainingPoint = 0;
            Transaction transaction = transactionsQ.poll();

            if(transaction.getPoints() <= spendPoint) {
                remainingPoint = transaction.getPoints();
                transaction.setPoints(0L);
            } else {
                remainingPoint = spendPoint;
                transaction.setPoints(transaction.getPoints() - remainingPoint);
            }
            pointsPerPayer.put(transaction.getPayer(), pointsPerPayer.get(transaction.getPayer()) - remainingPoint);
            transaction.setTimestamp(LocalDateTime.now());
            logger.info("Successfully updated user pointsperpayer Map");

            PointSpendResponse response = new PointSpendResponse();
            response.setPayer(transaction.getPayer());
            String displayPoint = remainingPoint > 0 ? "-" + remainingPoint : "0";
            response.setPoints(displayPoint);
            spendPointsPerPayerList.add(response);

            userTransactions.add(transaction);
            totalRewardPoints = totalRewardPoints - remainingPoint;
            spendPoint -= remainingPoint;

            user.setPointsPerPayer(pointsPerPayer);
        }

        transactionsQ.addAll(userTransactions);
        user.setTransactionsQueue(transactionsQ);
        user.setTotalRewardPoints(totalRewardPoints);
        logger.info("Successfully updated user total rewards and transaction queue");
        return spendPointsPerPayerList;
    }

}
