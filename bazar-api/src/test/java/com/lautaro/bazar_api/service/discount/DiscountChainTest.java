package com.lautaro.bazar_api.service.discount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DiscountChainTest {

    private WednesdayDiscountStrategy wednesdayStrategy;
    private BulkPurchaseDiscountStrategy bulkStrategy;
    private List<DiscountStrategy> strategies;

    @BeforeEach
    void setUp() {
        wednesdayStrategy = new WednesdayDiscountStrategy();
        bulkStrategy = new BulkPurchaseDiscountStrategy();
        strategies = List.of(wednesdayStrategy, bulkStrategy);
    }

    private double applyDiscounts(double subtotal, DayOfWeek day) {
        double total = subtotal;
        for (DiscountStrategy strategy : strategies) {
            if (strategy.isApplicable(subtotal, day)) {
                total = strategy.apply(total);
            }
        }
        return total;
    }

    @Test
    void shouldApplyBothDiscounts_whenWednesdayAndBulk() {
        // $250.000 miércoles -> -15% -> $212.500 -> -10% -> $191.250
        double result = applyDiscounts(250_000, DayOfWeek.WEDNESDAY);
        assertEquals(191_250.0, result, 0.01);
    }

    @Test
    void shouldApplyOnlyWednesday_whenBelowBulkThreshold() {
        // $100.000 miércoles -> -15% -> $85.000 (bulk no aplica)
        double result = applyDiscounts(100_000, DayOfWeek.WEDNESDAY);
        assertEquals(85_000.0, result, 0.01);
    }

    @Test
    void shouldApplyOnlyBulk_whenNotWednesday() {
        // $250.000 lunes -> -10% -> $225.000 (wednesday no aplica)
        double result = applyDiscounts(250_000, DayOfWeek.MONDAY);
        assertEquals(225_000.0, result, 0.01);
    }

    @Test
    void shouldApplyNoDiscount_whenNeitherConditionMet() {
        // $100.000 lunes -> sin descuento
        double result = applyDiscounts(100_000, DayOfWeek.MONDAY);
        assertEquals(100_000.0, result, 0.01);
    }
}