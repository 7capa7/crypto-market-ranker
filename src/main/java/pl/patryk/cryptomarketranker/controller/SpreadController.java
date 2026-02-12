package pl.patryk.cryptomarketranker.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.patryk.cryptomarketranker.service.SpreadRankingService;
import pl.patryk.cryptomarketranker.utils.RankingResponseDto;

@RestController
@RequestMapping("/spread")
public class SpreadController {


    private final SpreadRankingService spreadRankingService;

    public SpreadController(SpreadRankingService spreadRankingService) {
        this.spreadRankingService = spreadRankingService;
    }

    @PostMapping(value = "/calculate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RankingResponseDto> calculate() {
        RankingResponseDto rankingResponseDto = spreadRankingService.calculateAndStore();
        return ResponseEntity.ok(rankingResponseDto);
    }

    @GetMapping(value = "/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RankingResponseDto> ranking() {
        return spreadRankingService.getLastRanking()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
