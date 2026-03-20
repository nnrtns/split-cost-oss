package com.split;

import com.split.trip.Participant;
import com.split.trip.Trip;
import com.split.trip.accounts.CATEGORY;
import com.split.trip.accounts.SHARETYPE;
import com.split.trip.accounts.settler.Debt;
import com.split.trip.accounts.settler.factory.SettlerFactory;
import com.split.trip.accounts.Transaction;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.split.trip.accounts.settler.SettlementMode.*;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args ) {
        System.out.println( "Hello World! New Changes inComing from the Main class!" );

//        Trip trip = new Trip("Trip to Bali", Arrays.asList("John", "Jane", "Jack"), SimplifiedBalanceSheet.class);
        Participant p1 = new Participant("John");
        Participant p2 = new Participant("Jane");
        Participant p3 = new Participant("Jack");
        Trip trip = new Trip("Trip to Bali", Arrays.asList(p1, p2, p3));

        List<Transaction> listOfTransactions = Arrays.asList(
                new Transaction(300.0f, p1, CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(), Arrays.asList(
                        p1, p2, p3
                )));
        trip.addTransactions(listOfTransactions); // returns transaction ids as keys and the transaction details as the value
        List<Debt> finalSettlement = trip.settle(SettlerFactory.create(SIMPLIFIED));
        System.out.println(finalSettlement);

//        trip.addParticipants(Arrays.asList(new Participant("Pranav"), new Participant("Raj")));
//        trip.removeParticipants(Arrays.asList("John", "Jane", "Jack"));
//        trip.addTransactions(listOfTransactions); // returns transaction ids as keys and the transaction details as the value
//        trip.settle(SettlerFactory.create(SIMPLIFIED));
    }
}
