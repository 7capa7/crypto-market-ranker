package pl.patryk.cryptomarketranker.storage;

import pl.patryk.cryptomarketranker.utils.RankingResponseDto;

import java.util.Optional;

public interface RankingStorage {
    void save(RankingResponseDto rankingResponseDto);

    Optional<RankingResponseDto> get();
}
