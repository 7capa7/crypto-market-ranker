package pl.patryk.cryptomarketranker.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.patryk.cryptomarketranker.market.MarketDataProvider;
import pl.patryk.cryptomarketranker.storage.RankingStorage;
import pl.patryk.cryptomarketranker.utils.MarketSpreadDto;
import pl.patryk.cryptomarketranker.utils.OrderBook;
import pl.patryk.cryptomarketranker.utils.RankingDto;
import pl.patryk.cryptomarketranker.utils.RankingResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpreadRankingServiceTest {

    @Mock
    RankingStorage rankingStorage;

    @Mock
    MarketDataProvider marketDataProvider;

    private ExecutorService executor;

    @AfterEach
    void tearDown() {
        if (executor != null) executor.shutdownNow();
    }

    @Test
    void calculateAndStore_shouldReturnEmptyGroupsAndSave_whenNoMarkets() {
        executor = Executors.newSingleThreadExecutor();
        Semaphore limiter = new Semaphore(1, true);

        when(marketDataProvider.getAvailableMarkets()).thenReturn(List.of());

        SpreadRankingService service = new SpreadRankingService(
                rankingStorage, marketDataProvider, executor, limiter
        );

        RankingDto result = service.calculateAndStore();

        assertNotNull(result);
        assertNotNull(result);
        assertTrue(result.group1().isEmpty());
        assertTrue(result.group2().isEmpty());
        assertTrue(result.group3().isEmpty());

        verify(rankingStorage).save(any(RankingDto.class));
        verify(marketDataProvider).getAvailableMarkets();
        verifyNoMoreInteractions(marketDataProvider);
    }

    @Test
    void calculateAndStore_shouldGroupAndSortCorrectly_andSave() {
        executor = Executors.newFixedThreadPool(4);
        Semaphore limiter = new Semaphore(2, true);

        when(marketDataProvider.getAvailableMarkets()).thenReturn(List.of("Z_MARKET", "A_MARKET", "B_MARKET"));

        when(marketDataProvider.getTopOrderBook("A_MARKET"))
                .thenReturn(Optional.of(new OrderBook(new BigDecimal("99"), new BigDecimal("101"))));

        when(marketDataProvider.getTopOrderBook("B_MARKET"))
                .thenReturn(Optional.of(new OrderBook(new BigDecimal("4.2610"), new BigDecimal("4.5997"))));

        when(marketDataProvider.getTopOrderBook("Z_MARKET"))
                .thenReturn(Optional.empty());

        SpreadRankingService service = new SpreadRankingService(
                rankingStorage, marketDataProvider, executor, limiter
        );

        RankingDto result = service.calculateAndStore();

        assertEquals(1, result.group1().size());
        assertEquals(1, result.group2().size());
        assertEquals(1, result.group3().size());

        MarketSpreadDto g1 = result.group1().getFirst();
        assertEquals("A_MARKET", g1.market());
        assertEquals("2.00", g1.spread());

        MarketSpreadDto g2 = result.group2().getFirst();
        assertEquals("B_MARKET", g2.market());
        assertEquals("7.64", g2.spread());

        MarketSpreadDto g3 = result.group3().getFirst();
        assertEquals("Z_MARKET", g3.market());
        assertEquals("N/A", g3.spread());

        ArgumentCaptor<RankingDto> captor = ArgumentCaptor.forClass(RankingDto.class);
        verify(rankingStorage).save(captor.capture());
        RankingDto saved = captor.getValue();
        assertEquals(result, saved);

        verify(marketDataProvider).getAvailableMarkets();
        verify(marketDataProvider).getTopOrderBook("A_MARKET");
        verify(marketDataProvider).getTopOrderBook("B_MARKET");
        verify(marketDataProvider).getTopOrderBook("Z_MARKET");
    }

    @Test
    void calculateAndStore_shouldSortAlphabeticallyWithinEachGroup_caseInsensitive() {
        executor = Executors.newFixedThreadPool(6);
        Semaphore limiter = new Semaphore(3, true);

        when(marketDataProvider.getAvailableMarkets()).thenReturn(List.of("b_market", "A_market", "c_market"));

        when(marketDataProvider.getTopOrderBook("b_market"))
                .thenReturn(Optional.of(new OrderBook(new BigDecimal("10"), new BigDecimal("10"))));
        when(marketDataProvider.getTopOrderBook("A_market"))
                .thenReturn(Optional.of(new OrderBook(new BigDecimal("10"), new BigDecimal("10"))));
        when(marketDataProvider.getTopOrderBook("c_market"))
                .thenReturn(Optional.of(new OrderBook(new BigDecimal("10"), new BigDecimal("10"))));

        SpreadRankingService service = new SpreadRankingService(
                rankingStorage, marketDataProvider, executor, limiter
        );

        RankingDto result = service.calculateAndStore();

        assertEquals(3, result.group1().size());
        assertTrue(result.group2().isEmpty());
        assertTrue(result.group3().isEmpty());

        List<String> sortedMarkets = result.group1().stream()
                .map(MarketSpreadDto::market)
                .toList();

        assertEquals(List.of("A_market", "b_market", "c_market"), sortedMarkets);
    }

    @Test
    void calculateAndStore_shouldPutMarketIntoGroup3_whenProviderThrowsException() {
        executor = Executors.newSingleThreadExecutor();
        Semaphore limiter = new Semaphore(1, true);

        when(marketDataProvider.getAvailableMarkets()).thenReturn(List.of("BTC_USD"));
        when(marketDataProvider.getTopOrderBook("BTC_USD"))
                .thenThrow(new RuntimeException("boom"));

        SpreadRankingService service = new SpreadRankingService(
                rankingStorage, marketDataProvider, executor, limiter
        );

        RankingDto result = service.calculateAndStore();

        assertTrue(result.group1().isEmpty());
        assertTrue(result.group2().isEmpty());
        assertEquals(1, result.group3().size());
        assertEquals("BTC_USD", result.group3().getFirst().market());
        assertEquals("N/A", result.group3().getFirst().spread());

        verify(rankingStorage).save(any(RankingDto.class));
    }

}
