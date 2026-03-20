package com.split.expenseSplitter.controller;

import com.split.expenseSplitter.exception.GlobalExceptionHandler;
import com.split.expenseSplitter.exception.GlobalValidationExceptionHandler;
import com.split.expenseSplitter.service.TripTransactionService;
import com.split.expenseSplitter.support.TestFixtures;
import com.split.trip.accounts.Transaction;
import com.split.trip.accounts.CATEGORY;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripTransactionController.class)
@Import({GlobalExceptionHandler.class, GlobalValidationExceptionHandler.class})
class TripTransactionControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripTransactionService tripTransactionService;

    @Test
    void postTransaction_shouldReturnCreatedTransactions() throws Exception {
        Transaction tx = TestFixtures.transaction(
                "11111111-1111-1111-1111-111111111111",
                3030f,
                "P1",
                CATEGORY.FOOD,
                "01/01/2020",
                List.of("P1", "P2")
        );
        given(tripTransactionService.createTransaction(eq("trip-1"), any())).willReturn(List.of(tx));

        mockMvc.perform(post("/trip/trip-1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transactions": [
                                    {
                                      "spentAmount": 3030,
                                      "spentBy": "P1",
                                      "spentOn": "FOOD",
                                      "spentDate": "01/01/2020",
                                      "benefittedBy": ["P1", "P2"]
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$[0].spentBy.participantId").value("P1"));
    }

    @Test
    void postTransaction_shouldReturn400WhenTransactionsArrayIsEmpty() throws Exception {
        mockMvc.perform(post("/trip/trip-1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transactions": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.transactions").value("You must provide at least one transaction."));
    }

    @Test
    void postTransaction_shouldReturn400WhenSpentByIsBlank() throws Exception {
        mockMvc.perform(post("/trip/trip-1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transactions": [
                                    {
                                      "spentAmount": 3030,
                                      "spentBy": "",
                                      "spentOn": "FOOD",
                                      "spentDate": "01/01/2020",
                                      "benefittedBy": ["P1", "P2"]
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['transactions[0].spentBy']").value("You must provide a spent by."));
    }

    @Test
    void postTransaction_shouldReturn400WhenBenefittedByIsEmpty() throws Exception {
        mockMvc.perform(post("/trip/trip-1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transactions": [
                                    {
                                      "spentAmount": 3030,
                                      "spentBy": "P1",
                                      "spentOn": "FOOD",
                                      "spentDate": "01/01/2020",
                                      "benefittedBy": []
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['transactions[0].benefittedBy']").value("You must provide at least one benefitted by."));
    }

    @Test
    void getTransactions_shouldReturnAllTransactions() throws Exception {
        Transaction tx = TestFixtures.transaction(
                "11111111-1111-1111-1111-111111111111",
                1000f,
                "P1",
                CATEGORY.TRANSPORT,
                "01/01/2020",
                List.of("P1", "P2")
        );
        given(tripTransactionService.getTransactions("trip-1")).willReturn(List.of(tx));

        mockMvc.perform(get("/trip/trip-1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("11111111-1111-1111-1111-111111111111"));
    }

    @Test
    void getTransaction_shouldReturnSingleTransaction() throws Exception {
        Transaction tx = TestFixtures.transaction(
                "11111111-1111-1111-1111-111111111111",
                1000f,
                "P1",
                CATEGORY.TRANSPORT,
                "01/01/2020",
                List.of("P1", "P2")
        );
        given(tripTransactionService.getTransaction("trip-1", "11111111-1111-1111-1111-111111111111")).willReturn(tx);

        mockMvc.perform(get("/trip/trip-1/transactions/11111111-1111-1111-1111-111111111111"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("11111111-1111-1111-1111-111111111111"));
    }

    @Test
    void getTransaction_shouldMapRuntimeExceptionTo422() throws Exception {
        given(tripTransactionService.getTransaction("trip-1", "tx-1")).willThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/trip/trip-1/transactions/tx-1"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("boom"));
    }

    @Test
    void deleteTransaction_shouldReturnDeletedTransactionId() throws Exception {
        mockMvc.perform(delete("/trip/trip-1/transactions/tx-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("tx-1"));
    }
}