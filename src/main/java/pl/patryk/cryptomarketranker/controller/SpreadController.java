package pl.patryk.cryptomarketranker.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.patryk.cryptomarketranker.service.SpreadRankingService;
import pl.patryk.cryptomarketranker.utils.RankingDto;
import pl.patryk.cryptomarketranker.utils.RankingResponseDto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/spread")
public class SpreadController {


    private final SpreadRankingService spreadRankingService;

    public SpreadController(SpreadRankingService spreadRankingService) {
        this.spreadRankingService = spreadRankingService;
    }

    @PostMapping(value = "/calculate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RankingResponseDto> calculate() {
        RankingDto rankingDto = spreadRankingService.calculateAndStore();
        return ResponseEntity.ok(new RankingResponseDto(nowUtc(), rankingDto));
    }

    @GetMapping(value = "/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RankingResponseDto> ranking() {
        return spreadRankingService.getLastRanking()
                .map(item -> ResponseEntity.ok(
                        new RankingResponseDto(
                                nowUtc(),
                                item
                        )
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static Instant nowUtc() {
        return Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
