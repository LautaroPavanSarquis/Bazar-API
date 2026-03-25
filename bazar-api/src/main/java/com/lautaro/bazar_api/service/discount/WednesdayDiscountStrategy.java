package com.lautaro.bazar_api.service.discount;

import java.time.DayOfWeek;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class WednesdayDiscountStrategy implements DiscountStrategy {

    @Override
    public double apply(double currentTotal) {
        return currentTotal * 0.85; // 15% off
    }

    @Override
    public boolean isApplicable(double originalAmount, DayOfWeek day) {
        return day == DayOfWeek.WEDNESDAY;
    }
}

