# Fetch Rewards Points Systems
Contains the backend logic for point calculations

# How to run the project

## Pre-requisite
- Install any version above [Java 8](https://www.oracle.com/java/technologies/downloads/)
- Install any IDE of your choice (VS Code, Intellij, NetBeans)
- Postman to test APIs

## Run from IDE
- Simply run the PointsApplication.java
- Application will be running on http://localhost:8080
- Test the backend services using API testing tool

## APIS for testing

1. Check Balance Of User

#### API
```
http://localhost:8080/userpoints/v1/balance/{userId}
```
#### CURL
```
curl --location --request GET 'http://localhost:8080/userpoints/v1/balance/2'
```
#### RESPONSE
```
[
    "C:150"
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
    "2": [
        {
            "transactionId": "3328a0d6-6d4a-4fa8-b11c-c7c8b14c56db",
            "payer": "C",
            "points": 300,
            "timestamp": "2022-12-04T17:12:15.2119043"
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
curl --location --request GET 'http://localhost:8080/userpoints/v1/transactions/2'
```
#### RESPONSE
```
[
    {
        "transactionId": "3328a0d6-6d4a-4fa8-b11c-c7c8b14c56db",
        "payer": "C",
        "points": 300,
        "timestamp": "2022-12-04T17:12:15.2119043"
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
    "userId": 2, 
    "points": 150
}
```
#### RESPONSE
```
[
    {
        "payer": "C",
        "points": "-150"
    }
]
```
