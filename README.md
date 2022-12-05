# Fetch Rewards Coding Exercise - Reward Report System - Ajay Mohandas
Contains the backend logic for rewards calculations

## Goal

Users can view their reward balances, spend points and system to record every transaction as per payer, points and timestamp
### Rules
- We want the oldest points to be spent first (oldest based on transaction timestamp, not the order they’re received)
- We want no payer's points to go negative

### Expectation
- Add transactions for a specific payer and date.
- Spend points using the rules above and return a list for each call
- Return all payer point balances

Problem Statement - https://fetch-hiring.s3.us-east-1.amazonaws.com/points.pdf

## Assumptions
> No database to persist the data and hence each time you run the application user data would be null

> Basic validation are performed by the application, added spring security can be added but is not present in this application

> Since its a simple application, apis do not have any header or token for authorization

## How to run the project

### Pre-requisite
- Install any version above Java 8 but tried and tested in [Java 8](https://www.oracle.com/java/technologies/javase/javase8u211-later-archive-downloads.html) and [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html), 
- Install any IDE of your choice [VS Code](https://code.visualstudio.com/download), [Intellij](https://www.jetbrains.com/idea/download/#section=windows)
- [Postman](https://www.postman.com/downloads/) to test APIs

### Run from IDE
- Make sure IDE supports Spring Application, if not add plugins for Spring Application
- If using VS Code, go to marketplace and install plugins for Spring
- Clone the project using [GitHub](https://github.com/git-guides/install-git) to a local folder using `https://github.com/ajaymohandas89/FetchRewardsPointsSystems`
- Simply run the PointsApplication.java
- Application will be running on http://localhost:8080
- Test the backend services using API testing tool
- Import API collection in Postman using Import and upload FetchRewards_UserPointsProblem.postman_collection.json or test using below API curl commands

### Run Test Case
- From the project run test cases under UserPointServiceTest.java

### APIS for testing

1. Check Balance Of User

#### API
```
http://localhost:8080/userpoints/v1/balance/{userId}
```
#### CURL
```
curl --location --request GET 'http://localhost:8080/userpoints/v1/balance/1'
```
#### RESPONSE
```
[
    "UNILEVER:0",
    "MILLER COORS:5300",
    "DANNON:1000"
]
```

2. Fetch All Users Transactions

#### API
```
http://localhost:8080/userpoints/v1/transactions
```
#### CURL
```
curl --location --request GET 'http://localhost:8080/userpoints/v1/transactions'
```
#### RESPONSE
```
{
    "1": [
        {
            "transactionId": "4657090f-94f4-4993-8c84-4e2cbf794bd1",
            "payer": "DANNON",
            "points": 1000,
            "timestamp": "2022-12-04T22:07:36.9109449"
        },
        {
            "transactionId": "3445f577-aa00-48ef-b9c3-1b65425bc285",
            "payer": "DANNON",
            "points": 0,
            "timestamp": "2022-12-04T22:08:12.3789424"
        },
        {
            "transactionId": "c258d4a1-085c-42f9-adc1-a83026e6b945",
            "payer": "UNILEVER",
            "points": 0,
            "timestamp": "2022-12-04T22:08:12.3799424"
        },
        {
            "transactionId": "3e1216eb-96ab-4f55-9416-a6e6aee629ef",
            "payer": "MILLER COORS",
            "points": 5300,
            "timestamp": "2022-12-04T22:08:12.3809421"
        }
    ]
}
```

3. Fetch Each User Transactions

#### API
```
http://localhost:8080/userpoints/v1/transactions/{userId}
```
#### CURL
```
curl --location --request GET 'http://localhost:8080/userpoints/v1/transactions/1'
```
#### RESPONSE
```
[
    {
        "transactionId": "4657090f-94f4-4993-8c84-4e2cbf794bd1",
        "payer": "DANNON",
        "points": 1000,
        "timestamp": "2022-12-04T22:07:36.9109449"
    },
    {
        "transactionId": "3445f577-aa00-48ef-b9c3-1b65425bc285",
        "payer": "DANNON",
        "points": 0,
        "timestamp": "2022-12-04T22:08:12.3789424"
    },
    {
        "transactionId": "c258d4a1-085c-42f9-adc1-a83026e6b945",
        "payer": "UNILEVER",
        "points": 0,
        "timestamp": "2022-12-04T22:08:12.3799424"
    },
    {
        "transactionId": "3e1216eb-96ab-4f55-9416-a6e6aee629ef",
        "payer": "MILLER COORS",
        "points": 5300,
        "timestamp": "2022-12-04T22:08:12.3809421"
    }
]
```

4. Add Point For A User

#### API
```
http://localhost:8080/userpoints/v1/addPoints/{userId}
```
#### CURL
```
curl --location --request POST 'http://localhost:8080/userpoints/v1/addPoints/2' \
--header 'Content-Type: application/json' \
--data-raw '{
    "payer": "C", 
    "point": 300

}'
```
#### REQUEST
```
{
    "payer": "C", 
    "point": 300
}
```
#### RESPONSE
```
{
    "payer": "C",
    "points": 300,
    "timestamp": "2022-12-04T17:12:15.2119043"
}
```

5. Spend Point For A User

#### API
```
http://localhost:8080/userpoints/v1/spend
```
#### CURL
```
curl --location --request PUT 'http://localhost:8080/userpoints/v1/spend' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userId": 2, 
    "points": 150
}'
```
#### REQUEST
```
{
    "userId": 1, 
    "points": 5000
}
```
#### RESPONSE
```
[
    {
        "payer": "DANNON",
        "points": "-100"
    },
    {
        "payer": "UNILEVER",
        "points": "-200"
    },
    {
        "payer": "MILLER COORS",
        "points": "-4700"
    }
]
```
