package pl.patryk.cryptomarketranker.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class SpreadCalculator {

    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);
    private static final BigDecimal HALF = new BigDecimal("0.5");
    private static final BigDecimal HUNDRED = new BigDecimal("100");


    private SpreadCalculator() {
    }

    public static BigDecimal spreadPercent(BigDecimal bid, BigDecimal ask) {
        if (bid == null || ask == null) throw new IllegalArgumentException("bid/ask required");
        if (bid.signum() <= 0 || ask.signum() <= 0) throw new IllegalArgumentException("bid/ask must be > 0");
        if (ask.compareTo(bid) < 0) throw new IllegalArgumentException("ask < bid");

        BigDecimal numerator = ask.subtract(bid, MC);
        BigDecimal denominator = HALF.multiply(ask.add(bid, MC), MC);

        if (denominator.signum() == 0) throw new IllegalArgumentException("denominator=0");

        BigDecimal raw = numerator.divide(denominator, MC).multiply(HUNDRED, MC);
        return raw.setScale(2, RoundingMode.HALF_UP);
    }
}
