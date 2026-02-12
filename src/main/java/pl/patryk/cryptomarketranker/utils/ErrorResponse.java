package pl.patryk.cryptomarketranker.utils;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ErrorResponse unauthorized(String path, String message) {
        return new ErrorResponse(
                Instant.now(),
                401,
                "unauthorized",
                message,
                path
        );
    }
}
