package pl.patryk.cryptomarketranker.market;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pl.patryk.cryptomarketranker.market.parser.MarketDataParser;
import pl.patryk.cryptomarketranker.utils.OrderBook;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

@Component
public class KangaMarketDataProvider implements MarketDataProvider {

    private final RestClient restClient;
    private final MarketDataParser marketDataParser;

    public KangaMarketDataProvider(RestClient restClient, MarketDataParser marketDataParser) {
        this.restClient = restClient;
        this.marketDataParser = marketDataParser;
    }

    @Override
    public List<String> getAvailableMarkets() {
        try {
            JsonNode root = restClient.get()
                    .uri("/api/market/pairs")
                    .retrieve()
                    .body(JsonNode.class);

            return marketDataParser.parseAvailableMarkets(root);

        } catch (Exception ex) {
            return List.of();
        }
    }

    @Override
    public Optional<OrderBook> getTopOrderBook(String market) {
        try {
            JsonNode root = restClient.get()
                    .uri("/api/market/orderbook/{market}", market)
                    .retrieve()
                    .body(JsonNode.class);

            return marketDataParser.parseTopOrderBook(root);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }


}
