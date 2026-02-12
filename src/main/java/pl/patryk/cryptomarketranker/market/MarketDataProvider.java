package pl.patryk.cryptomarketranker.market;

import pl.patryk.cryptomarketranker.utils.OrderBook;

import java.util.List;
import java.util.Optional;

public interface MarketDataProvider {
    List<String> getAvailableMarkets();

    Optional<OrderBook> getTopOrderBook(String market);
}
