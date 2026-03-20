package com.split.expenseSplitter.service.impl;

import com.split.expenseSplitter.repository.SettlementRepository;
import com.split.trip.Trip;
import com.split.trip.accounts.settler.Debt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementServiceImplTest {

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private Trip trip;

    @InjectMocks
    private SettlementServiceImpl settlementService;

    @Test
    void settle_shouldUseSimplifiedModeAndPersist() {
        List<Debt> debts = List.of(new Debt("P2", "P1", 10f));
        when(settlementRepository.getTrip("trip-1")).thenReturn(trip);
        when(trip.settle(org.mockito.ArgumentMatchers.argThat(settler -> settler != null))).thenReturn(debts);

        List<Debt> result = settlementService.settle("trip-1", true);

        assertEquals(debts, result);
        verify(settlementRepository).persistsettlement("trip-1", debts);
    }

    @Test
    void settle_shouldUseBasicModeAndPersist() {
        List<Debt> debts = List.of(new Debt("P3", "P1", 12f));
        when(settlementRepository.getTrip("trip-2")).thenReturn(trip);
        when(trip.settle(org.mockito.ArgumentMatchers.argThat(settler -> settler != null))).thenReturn(debts);

        List<Debt> result = settlementService.settle("trip-2", false);

        assertEquals(debts, result);
        verify(settlementRepository).persistsettlement("trip-2", debts);
    }

    @Test
    void settle_shouldNotPersistWhenTripLoadFails() {
        when(settlementRepository.getTrip("trip-1")).thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class, () -> settlementService.settle("trip-1", true));
        verify(settlementRepository, never()).persistsettlement(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void settle_shouldNotPersistWhenTripSettleFails() {
        when(settlementRepository.getTrip("trip-1")).thenReturn(trip);
        when(trip.settle(org.mockito.ArgumentMatchers.argThat(settler -> settler != null))).thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class, () -> settlementService.settle("trip-1", true));
        verify(settlementRepository, never()).persistsettlement(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyList());
    }
}
