package com.lautaro.bazar_api.service.discount;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import static org.junit.jupiter.api.Assertions.*;

class WednesdayDiscountStrategyTest {

    private WednesdayDiscountStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new WednesdayDiscountStrategy();
    }

    @Test
    void isApplicable_shouldReturnTrue_whenWednesday() {
        assertTrue(strategy.isApplicable(100_000, DayOfWeek.WEDNESDAY));
    }

    @Test
    void isApplicable_shouldReturnFalse_whenNotWednesday() {
        assertFalse(strategy.isApplicable(100_000, DayOfWeek.MONDAY));
        assertFalse(strategy.isApplicable(100_000, DayOfWeek.FRIDAY));
    }

    @Test
    void apply_shouldApply15PercentDiscount() {
        double result = strategy.apply(100_000);
        assertEquals(85_000.0, result, 0.01);
    }
}
