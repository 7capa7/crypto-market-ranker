package pl.patryk.cryptomarketranker.utils;

import java.time.Instant;

public record RankingResponseDto(
        Instant timestamp,
        RankingDto ranking
) {}