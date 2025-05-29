package faang.school.postservice.model.analytic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Interval {
    MINUTES_10(600),
    MINUTES_20(1200),
    MINUTES_30(1800),
    MINUTES_40(2400),
    MINUTES_50(3000),
    HOUR_1(3600),
    HOUR_2(7200),
    HOUR_3(10800),
    HOUR_4(14400),
    HOUR_5(18000),
    HOUR_6(21600),
    HOUR_12(43200),
    MONTH_1(2592000),
    MONTH_2(5184000),
    MONTH_3(7776000),
    MONTH_4(10368000),
    MONTH_5(12960000),
    MONTH_6(15552000),
    MONTH_12(31104000),
    YEAR_1(31536000),
    YEAR_2(63072000);

    private final int seconds;
}
