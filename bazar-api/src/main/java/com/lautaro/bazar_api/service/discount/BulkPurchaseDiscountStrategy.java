package com.lautaro.bazar_api.service.discount;

import java.time.DayOfWeek;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class BulkPurchaseDiscountStrategy implements DiscountStrategy {

    private static final double MIN_BULK_AMOUNT = 200_000.0;

    @Override
    public double apply(double currentTotal) {
        return currentTotal * 0.90; // 10% off
    }

    @Override
    public boolean isApplicable(double originalAmount, DayOfWeek day) {
        return originalAmount >= MIN_BULK_AMOUNT;
    }
}

