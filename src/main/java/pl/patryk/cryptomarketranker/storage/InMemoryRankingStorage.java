package pl.patryk.cryptomarketranker.storage;

import org.springframework.stereotype.Component;
import pl.patryk.cryptomarketranker.utils.RankingDto;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class InMemoryRankingStorage implements RankingStorage {

    private final AtomicReference<RankingDto> reference = new AtomicReference<>();

    @Override
    public void save(RankingDto rankingDto) {
        reference.set(rankingDto);
    }

    @Override
    public Optional<RankingDto> get() {
        return Optional.ofNullable(reference.get());
    }
}
