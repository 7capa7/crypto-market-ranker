package pl.patryk.cryptomarketranker.storage;

import org.springframework.stereotype.Component;
import pl.patryk.cryptomarketranker.utils.RankingResponseDto;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class InMemoryRankingStorage implements RankingStorage {

    private final AtomicReference<RankingResponseDto> reference = new AtomicReference<>();

    @Override
    public void save(RankingResponseDto rankingResponseDto) {
        reference.set(rankingResponseDto);
    }

    @Override
    public Optional<RankingResponseDto> get() {
        return Optional.ofNullable(reference.get());
    }
}
