package Eco.TradeX.business.utils.CandleUtils;

import ru.tinkoff.piapi.contract.v1.CandleInterval;

public class CandleIntervalConverter {
    public static int toSeconds(CandleInterval interval) {
        return switch (interval) {
            case CANDLE_INTERVAL_1_MIN -> 60;
            case CANDLE_INTERVAL_2_MIN -> 120;
            case CANDLE_INTERVAL_3_MIN -> 180;
            case CANDLE_INTERVAL_5_MIN -> 300;
            case CANDLE_INTERVAL_10_MIN -> 600;
            case CANDLE_INTERVAL_15_MIN -> 900;
            case CANDLE_INTERVAL_30_MIN -> 1800;
            case CANDLE_INTERVAL_HOUR -> 3600;
            case CANDLE_INTERVAL_2_HOUR -> 7200;
            case CANDLE_INTERVAL_4_HOUR -> 14400;
            case CANDLE_INTERVAL_DAY -> 86400;
            case CANDLE_INTERVAL_WEEK -> 604800;
            case CANDLE_INTERVAL_MONTH -> 2629746;
            default -> 80000;
        };
    }

    public static int toMaximumFetchPeriodInSeconds(CandleInterval interval) {
        return switch (interval) {
            case CANDLE_INTERVAL_1_MIN -> 86400;
            case CANDLE_INTERVAL_2_MIN -> 86400;
            case CANDLE_INTERVAL_3_MIN -> 86400;
            case CANDLE_INTERVAL_5_MIN -> 86400;
            case CANDLE_INTERVAL_10_MIN -> 86400;
            case CANDLE_INTERVAL_15_MIN -> 86400;
            case CANDLE_INTERVAL_30_MIN -> 172800;
            case CANDLE_INTERVAL_HOUR -> 604800;
            case CANDLE_INTERVAL_2_HOUR -> 2629746;
            case CANDLE_INTERVAL_4_HOUR -> 604800;
            case CANDLE_INTERVAL_DAY -> 31536000;
            case CANDLE_INTERVAL_WEEK -> 31536000;
            case CANDLE_INTERVAL_MONTH -> 63072000;
            default -> 80000;
        };
    }

    public static int toMinutesOnlyMinutePeriod(CandleInterval interval) {
        return switch (interval) {
            case CANDLE_INTERVAL_1_MIN -> 1;
            case CANDLE_INTERVAL_2_MIN -> 2;
            case CANDLE_INTERVAL_3_MIN -> 3;
            case CANDLE_INTERVAL_5_MIN -> 5;
            case CANDLE_INTERVAL_10_MIN -> 10;
            case CANDLE_INTERVAL_15_MIN -> 15;
            case CANDLE_INTERVAL_30_MIN -> 30;
            default -> -1;
        };
    }

    public static int toHoursOnlyHoursPeriod(CandleInterval interval) {
        return switch (interval) {
            case CANDLE_INTERVAL_HOUR -> 1;
            case CANDLE_INTERVAL_2_HOUR -> 2;
            case CANDLE_INTERVAL_4_HOUR -> 4;
            default -> -1;
        };
    }

    public static int toDaysOnlyDaysPeriod(CandleInterval interval) {
        return switch (interval) {
            case CANDLE_INTERVAL_DAY -> 1;
            default -> -1;
        };
    }

    public static int toWeeksOnlyWeeksPeriod(CandleInterval interval) {
        return switch (interval) {
            case CANDLE_INTERVAL_WEEK -> 1;
            default -> -1;
        };
    }

    public static int toMonthsOnlyMonthsPeriod(CandleInterval interval) {
        return switch (interval) {
            case CANDLE_INTERVAL_MONTH -> 1;
            default -> -1;
        };
    }
}
