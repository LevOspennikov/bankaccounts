# bankaccounts

This is a RESTful api for creating and transfering some virtual money. It uses in-memory storage for account and history, works with 4 threads

### Available Services

| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| GET | /account/{accountId}/balance | get balance for accountId | 
| GET | /account/{accountId}/history | get history of account | 
| POST | /account/create | create a new account | 
| POST | /account/withdraw | withdraw money from account | 
| POST | /account/deposit | deposit money to account | 
| POST | /transaction | perform transaction between 2 accounts | 

### Http Status
- 200 OK: The request has succeeded
- 400 Bad Request: The request could not be understood by the server or contains bad params
- 404 Not Found: The requested resource cannot be found
- 500 Internal Error: The request couldn't be processed right now
