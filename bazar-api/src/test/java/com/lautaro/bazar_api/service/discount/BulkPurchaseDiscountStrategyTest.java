
package com.lautaro.bazar_api.service.discount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

public class BulkPurchaseDiscountStrategyTest {

    private BulkPurchaseDiscountStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BulkPurchaseDiscountStrategy();
    }

    @Test
    void isApplicable_shouldReturnTrue_whenAmountGreaterThan200k() {
        assertTrue(strategy.isApplicable(250_000, DayOfWeek.MONDAY));
    }

    @Test
    void isApplicable_shouldReturnTrue_whenAmountIsExactly200k() {
        assertTrue(strategy.isApplicable(200_000, DayOfWeek.FRIDAY));
    }

    @Test
    void isApplicable_shouldReturnFalse_whenAmountLessThan200k() {
        assertFalse(strategy.isApplicable(199_999, DayOfWeek.WEDNESDAY));
    }

    @Test
    void apply_shouldApply10PercentDiscount() {
        double result = strategy.apply(200_000);
        assertEquals(180_000.0, result, 0.01);
    }

    @Test
    void apply_shouldApply10PercentDiscount_forAnyAmountPassed() {
        double result = strategy.apply(300_000);
        assertEquals(270_000.0, result, 0.01);
    }
}
