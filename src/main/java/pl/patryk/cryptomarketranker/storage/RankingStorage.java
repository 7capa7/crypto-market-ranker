package pl.patryk.cryptomarketranker.storage;

import pl.patryk.cryptomarketranker.utils.RankingDto;

import java.util.Optional;

public interface RankingStorage {
    void save(RankingDto rankingDto);

    Optional<RankingDto> get();
}
