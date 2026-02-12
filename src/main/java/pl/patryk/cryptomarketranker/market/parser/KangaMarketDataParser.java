package pl.patryk.cryptomarketranker.market.parser;

import org.springframework.stereotype.Component;
import pl.patryk.cryptomarketranker.utils.OrderBook;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class KangaMarketDataParser implements MarketDataParser {

    /*
     * INFO!
     * According to Kanga API observations bids and asks are returned already sorted
     * But since this is a public API and the contract does not explicitly guarantee ordering, I defensively select the best price by sorting.
     * If I could trust API sort asks/bids I would use this method
     */
//    private static BigDecimal bestPriceFromLevels(JsonNode levels) {
//        if (levels == null || !levels.isArray() || levels.isEmpty()) {
//            return null;
//        }
//
//        JsonNode bestLevel = levels.get(0);
//        if (bestLevel == null || !bestLevel.isArray() || bestLevel.size() < 1) {
//            return null;
//        }
//
//        JsonNode priceNode = bestLevel.get(0);
//        if (priceNode == null || priceNode.isNull()) {
//            return null;
//        }
//
//        String price = priceNode.asText().trim();
//        if (price.isEmpty()) return null;
//
//        try {
//            return new BigDecimal(price);
//        } catch (NumberFormatException e) {
//            return null;
//        }
//    }


    @Override
    public Optional<OrderBook> parseTopOrderBook(JsonNode root) {
        if (root == null || !root.isObject()) return Optional.empty();

        BigDecimal bestBid = bestPriceFromLevels(root.get("bids"), true);
        BigDecimal bestAsk = bestPriceFromLevels(root.get("asks"), false);

        if (bestBid == null || bestAsk == null) return Optional.empty();
        if (bestBid.signum() <= 0 || bestAsk.signum() <= 0) return Optional.empty();
        if (bestAsk.compareTo(bestBid) < 0) return Optional.empty();

        return Optional.of(new OrderBook(bestBid, bestAsk));
    }

    @Override
    public List<String> parseAvailableMarkets(JsonNode root) {
        if (root == null || !root.isArray()) {
            return List.of();
        }

        return StreamSupport.stream(root.spliterator(), false)
                .map(node -> node.get("ticker_id"))
                .filter(Objects::nonNull)
                .map(JsonNode::asText)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private static BigDecimal bestPriceFromLevels(JsonNode levels, boolean isBid) {
        if (levels == null || !levels.isArray() || levels.isEmpty()) return null;

        return StreamSupport.stream(levels.spliterator(), false)
                .map(KangaMarketDataParser::extractPrice)
                .filter(Objects::nonNull)
                .filter(p -> p.signum() > 0)
                .sorted(isBid ? Comparator.reverseOrder() : Comparator.naturalOrder())
                .findFirst()
                .orElse(null);
    }

    private static BigDecimal extractPrice(JsonNode level) {
        if (level == null || !level.isArray() || level.size() < 1) return null;

        JsonNode priceNode = level.get(0);
        if (priceNode == null || priceNode.isNull()) return null;

        try {
            return new BigDecimal(priceNode.asText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}