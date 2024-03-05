package Eco.TradeX.business;

import Eco.TradeX.domain.CandleData;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface GetStrategyParamsUseCase {
    String getStrategyName();
    void initializeContainerForCandleLiveStreaming(String figi, CandleInterval interval);
    Map<String, BigDecimal> calculateParametersForCandle(CandleData candle);
    Map<String, List<BigDecimal>> getStrategyParametersForCandles(List<CandleData> candles, Instant from, Instant to, String figi, CandleInterval interval);
}
