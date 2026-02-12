package pl.patryk.cryptomarketranker.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spread")
public class SpreadController {
    @PostMapping(value = "/calculate", produces = MediaType.APPLICATION_JSON_VALUE)
    public String calculate() {
        return "Test calculate";
    }

    @GetMapping(value = "/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
    public String ranking() {
        return "Test ranking";
    }
}
