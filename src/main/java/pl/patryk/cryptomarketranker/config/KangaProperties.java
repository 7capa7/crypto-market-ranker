package pl.patryk.cryptomarketranker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "kanga")
public record KangaProperties(
        String baseUrl,
        Http http,
        int concurrencyLimit
) {
    public record Http(
            Duration connectTimeout,
            Duration readTimeout
    ) {}
}