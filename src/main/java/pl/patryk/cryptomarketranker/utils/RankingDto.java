package pl.patryk.cryptomarketranker.utils;

import java.util.List;

public record RankingDto(
        List<MarketSpreadDto> group1,
        List<MarketSpreadDto> group2,
        List<MarketSpreadDto> group3
) {}