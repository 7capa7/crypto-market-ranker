package pl.patryk.cryptomarketranker.service;

import org.springframework.stereotype.Service;
import pl.patryk.cryptomarketranker.market.MarketDataProvider;
import pl.patryk.cryptomarketranker.storage.RankingStorage;
import pl.patryk.cryptomarketranker.utils.MarketSpreadDto;
import pl.patryk.cryptomarketranker.utils.OrderBook;
import pl.patryk.cryptomarketranker.utils.RankingDto;
import pl.patryk.cryptomarketranker.utils.RankingResponseDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

@Service
public class SpreadRankingService {
    private static final BigDecimal PERCENT_2 = new BigDecimal("2.00");

    private final RankingStorage rankingStorage;
    private final MarketDataProvider marketDataProvider;
    private final ExecutorService executor;
    private final Semaphore limiter;

    public SpreadRankingService(RankingStorage rankingStorage, MarketDataProvider marketDataProvider, ExecutorService executor, Semaphore limiter) {
        this.rankingStorage = rankingStorage;
        this.marketDataProvider = marketDataProvider;
        this.executor = executor;
        this.limiter = limiter;
    }

    public Optional<RankingResponseDto> getLastRanking() {
        return rankingStorage.get();
    }

    public RankingResponseDto calculateAndStore() {
        Instant now = Instant.now();

        List<String> markets = marketDataProvider.getAvailableMarkets();
        if (markets.isEmpty()) {

            RankingResponseDto empty = new RankingResponseDto(now, new RankingDto(List.of(), List.of(), List.of()));
            rankingStorage.save(empty);
            return empty;
        }

        List<CompletableFuture<MarketResult>> futures = markets.stream()
                .map(market -> CompletableFuture.supplyAsync(() -> computeOneMarket(market), executor))
                .toList();

        List<MarketResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        ArrayList<MarketSpreadDto> group1 = new ArrayList<>();
        ArrayList<MarketSpreadDto> group2 = new ArrayList<>();
        ArrayList<MarketSpreadDto> group3 = new ArrayList<>();

        for (MarketResult r : results) {
            if (r.spreadPercent() == null) {
                group3.add(new MarketSpreadDto(r.market(), "N/A"));
                continue;
            }

            String spreadStr = r.spreadPercent().toPlainString();
            MarketSpreadDto marketSpreadDto = new MarketSpreadDto(r.market(), spreadStr);

            if (r.spreadPercent().compareTo(PERCENT_2) <= 0) group1.add(marketSpreadDto);
            else group2.add(marketSpreadDto);
        }

        Comparator<MarketSpreadDto> byMarket =
                Comparator.comparing(MarketSpreadDto::market, String.CASE_INSENSITIVE_ORDER);

        group1.sort(byMarket);
        group2.sort(byMarket);
        group3.sort(byMarket);

        RankingResponseDto snapshot = new RankingResponseDto(
                now,
                new RankingDto(
                        List.copyOf(group1),
                        List.copyOf(group2),
                        List.copyOf(group3)
                )
        );

        rankingStorage.save(snapshot);
        return snapshot;
    }

    private record MarketResult(String market, BigDecimal spreadPercent) {
        static MarketResult ok(String market, BigDecimal spreadPercent) {
            return new MarketResult(market, spreadPercent);
        }

        static MarketResult nan(String market) {
            return new MarketResult(market, null);
        }
    }

    private MarketResult computeOneMarket(String market) {
        boolean acquired = false;
        try {
            limiter.acquire();
            acquired = true;

            Optional<OrderBook> obOpt = marketDataProvider.getTopOrderBook(market);
            if (obOpt.isEmpty()) return MarketResult.nan(market);

            OrderBook ob = obOpt.get();
            BigDecimal spread = SpreadCalculator.spreadPercent(ob.bid(), ob.ask());

            return MarketResult.ok(market, spread);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return MarketResult.nan(market);
        } catch (Exception e) {
            return MarketResult.nan(market);
        } finally {
            if (acquired) limiter.release();
        }
    }

}
