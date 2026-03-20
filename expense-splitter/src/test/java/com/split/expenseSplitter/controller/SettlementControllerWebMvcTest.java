package com.split.expenseSplitter.controller;

import com.split.expenseSplitter.service.SettlementService;
import com.split.trip.accounts.settler.Debt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SettlementController.class)
class SettlementControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SettlementService settlementService;

    @Test
    void settle_shouldReturnMappedDebtsForSimplifiedMode() throws Exception {
        given(settlementService.settle("trip-1", true))
                .willReturn(List.of(new Debt("P2", "P1", 12.5f)));

        mockMvc.perform(post("/trip/trip-1/settlement").queryParam("simplify", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].from").value("P2"))
                .andExpect(jsonPath("$[0].to").value("P1"))
                .andExpect(jsonPath("$[0].amount").value("12.5"));
    }

    @Test
    void settle_shouldReturnMappedDebtsForBasicMode() throws Exception {
        given(settlementService.settle("trip-1", false))
                .willReturn(List.of(new Debt("P3", "P1", 9.25f)));

        mockMvc.perform(post("/trip/trip-1/settlement").queryParam("simplify", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].from").value("P3"))
                .andExpect(jsonPath("$[0].amount").value("9.25"));
    }

    @Test
    void settle_shouldReturn400WhenSimplifyFlagIsMissing() throws Exception {
        mockMvc.perform(post("/trip/trip-1/settlement"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void settle_shouldTranslateRuntimeExceptionsTo422() throws Exception {
        given(settlementService.settle("trip-1", false)).willThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/trip/trip-1/settlement").queryParam("simplify", "false"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("boom"));
    }
}
