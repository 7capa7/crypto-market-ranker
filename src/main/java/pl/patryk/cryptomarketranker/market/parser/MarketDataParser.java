package pl.patryk.cryptomarketranker.market.parser;

import pl.patryk.cryptomarketranker.utils.OrderBook;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

public interface MarketDataParser {
    Optional<OrderBook> parseTopOrderBook(JsonNode orderBookRoot);
    List<String> parseAvailableMarkets(JsonNode marketsRoot);
}
