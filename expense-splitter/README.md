# Expense Splitter
## Context
- Trip: In the context of a defined trip, there can be multiple transactions.
- Transaction: Defines the amount of money spent on a specific activity and the details of who spent it. ```(cost, spentBy, spentOn)```

## Actors
- Individuals involved in the trip.

## Available Features
- Split the expenses between the participants in a rudimentrary way such that each knows how much to pay the other.
- Compute the split such that settlements are simplified and there is the least number of transactions to settle debts among participants.

## APIs
- POST /trip :
  - returns a system generated id for this trip as an acknowledgement.
  - Input below
```aiignore
{
    "name": "Trip to the City",
    "participants": ["Alice",...]
}
```
- PUT /trip/{trip_id} :
```
{
    "addParticipants": ["Bob",...],
    "removeParticipants":["Alice",...]
}  
```
- POST /trip/{trip_id}/transactions
  - You can't edit a transaction, rather you can delete it and add new ones again.
```aiignore
{
    "transactions": [
            {
                "SpentAmount": 100,
                "spentBy": "Alice",
                "spentOn": "TRANSPORTATION",
                "comments": "Spent on the Flight ticket",
                "spentDate": "2020-01-01",
                "benefittedBy": ["Bob", "Bobby"]
            },
            .
            .
            .
        ]
}
```
- DELETE /trip/{trip_id}/transactions
  - Deletion could happen based on the date or the participant. Participants are mandatory and the date is optional.
  - If we maintain a unique id for every transaction, we'll end up having a burst of transaction ids. Overhead to avoid in the first release. 
```aiignore
{
    "transaction_id": "xyzabc123"
}
```

- GET /trip/{trip_id}/transactions
```aiignore
{
    "spentBy": "Alice",
    "spentOn": "TRANSPORTATION",
    "spentDate": "2020-01-01"
}
```

- GET /trips<br>
    - <i>Returns a list of all trips that have been created in this system</i>

- GET /trips/{trip_id}/details
    - <i>Returns a list of all trips that have been created in this system</i>

- POST trips/{trip_id}/settlement
    - returns the details of the settlements.
```aiignore
{
    "simplify" : true
}
```

## Story for using the APIs
- Create a trip using the POST /trip API.
- Add participants to the trip using the PUT /trip/{trip_id} API.
- Add transactions to the trip using the POST /trip/{trip_id}/transactions API.
- Get the details of the trip using the GET /trip/{trip_id}/details API.
- Get the details of all trips using the GET /trips API.
- Get the details of all trips using the GET /trips/{trip_id}/details API.
- Get the settlement details of the trip using the POST /trip/{trip_id}/settlement API.