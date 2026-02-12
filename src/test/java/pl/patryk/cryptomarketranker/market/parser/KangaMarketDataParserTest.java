package pl.patryk.cryptomarketranker.market.parser;

import org.junit.jupiter.api.Test;
import pl.patryk.cryptomarketranker.utils.OrderBook;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KangaMarketDataParserTest {

    private final ObjectMapper om = new ObjectMapper();
    private final KangaMarketDataParser parser = new KangaMarketDataParser();

    @Test
    void parseAvailableMarkets_shouldReturnSortedDistinctNonBlankTickers() {
        String json = """
                [
                  {"ticker_id":"ETH_USD"},
                  {"ticker_id":" BTC_USD "},
                  {"ticker_id":""},
                  {"ticker_id":"ETH_USD"},
                  {"ticker_id":"algo_usd"},
                  {"ticker_id":"BTC_USD"}
                ]
                """;

        JsonNode root = om.readTree(json);

        List<String> markets = parser.parseAvailableMarkets(root);

        assertEquals(List.of("algo_usd", "BTC_USD", "ETH_USD"), markets);
    }

    @Test
    void parseAvailableMarkets_shouldReturnEmpty_whenRootIsNull() {
        assertEquals(List.of(), parser.parseAvailableMarkets(null));
    }


    @Test
    void parseTopOrderBook_shouldPickBestBidHighestAndBestAskLowest() {
        String json = """
                {
                  "bids": [["10","1"], ["12","1"], ["11","1"]],
                  "asks": [["20","1"], ["18","1"], ["19","1"]]
                }
                """;

        JsonNode root = om.readTree(json);

        OrderBook ob = parser.parseTopOrderBook(root).orElseThrow();

        assertEquals(new BigDecimal("12"), ob.bid());
        assertEquals(new BigDecimal("18"), ob.ask());
    }

    @Test
    void parseTopOrderBook_shouldReturnEmpty_whenMissingBids() {
        String json = """
                { "asks": [["20","1"]] }
                """;
        JsonNode root = om.readTree(json);

        assertTrue(parser.parseTopOrderBook(root).isEmpty());
    }

    @Test
    void parseTopOrderBook_shouldReturnEmpty_whenMissingAsks() {
        String json = """
                { "bids": [["10","1"]] }
                """;
        JsonNode root = om.readTree(json);

        assertTrue(parser.parseTopOrderBook(root).isEmpty());
    }

    @Test
    void parseTopOrderBook_shouldReturnEmpty_whenBidsEmpty() {
        String json = """
                { "bids": [], "asks": [["20","1"]] }
                """;
        JsonNode root = om.readTree(json);

        assertTrue(parser.parseTopOrderBook(root).isEmpty());
    }

    @Test
    void parseTopOrderBook_shouldReturnEmpty_whenAsksEmpty() {
        String json = """
                { "bids": [["10","1"]], "asks": [] }
                """;
        JsonNode root = om.readTree(json);

        assertTrue(parser.parseTopOrderBook(root).isEmpty());
    }


    @Test
    void parseTopOrderBook_shouldReturnEmpty_whenPricesNonPositive() {
        String json = """
                { "bids": [["0","1"]], "asks": [["20","1"]] }
                """;
        JsonNode root = om.readTree(json);

        assertTrue(parser.parseTopOrderBook(root).isEmpty());
    }

    @Test
    void parseTopOrderBook_shouldReturnEmpty_whenAskLessThanBid() {
        String json = """
                { "bids": [["10","1"]], "asks": [["9","1"]] }
                """;
        JsonNode root = om.readTree(json);

        assertTrue(parser.parseTopOrderBook(root).isEmpty());
    }

    @Test
    void parseTopOrderBook_shouldReturnEmpty_whenRootIsNull() {
        assertTrue(parser.parseTopOrderBook(null).isEmpty());
    }
}
