package pl.patryk.cryptomarketranker.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class SpreadCalculatorTest {
    @Test
    void shouldCalculateSpread() {
        BigDecimal bid = new BigDecimal("4.2610");
        BigDecimal ask = new BigDecimal("4.5997");

        BigDecimal spread = SpreadCalculator.spreadPercent(bid, ask);

        assertEquals(new BigDecimal("7.64"), spread);
    }

    @Test
    void shouldReturnZeroWhenAskEqualsBid() {
        BigDecimal bid = new BigDecimal("10.00");
        BigDecimal ask = new BigDecimal("10.00");

        assertEquals(new BigDecimal("0.00"), SpreadCalculator.spreadPercent(bid, ask));
    }

    @Test
    void shouldThrowWhenAskLessThanBid() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadCalculator.spreadPercent(new BigDecimal("10.00"), new BigDecimal("9.99"))
        );
        assertTrue(ex.getMessage().toLowerCase().contains("ask < bid"));
    }

    @Test
    void shouldThrowWhenBidOrAskNonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> SpreadCalculator.spreadPercent(new BigDecimal("0"), new BigDecimal("1")));

        assertThrows(IllegalArgumentException.class,
                () -> SpreadCalculator.spreadPercent(new BigDecimal("-1"), new BigDecimal("1")));

        assertThrows(IllegalArgumentException.class,
                () -> SpreadCalculator.spreadPercent(new BigDecimal("1"), new BigDecimal("0")));
    }

    @Test
    void shouldThrowWhenNullArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> SpreadCalculator.spreadPercent(null, new BigDecimal("1")));

        assertThrows(IllegalArgumentException.class,
                () -> SpreadCalculator.spreadPercent(new BigDecimal("1"), null));
    }
}
