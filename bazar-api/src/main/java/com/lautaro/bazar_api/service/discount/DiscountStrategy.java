package com.lautaro.bazar_api.service.discount;

import java.time.DayOfWeek;

public interface DiscountStrategy {

    double apply(double currentTotal);

    boolean isApplicable(double originalAmount, DayOfWeek day);
}

