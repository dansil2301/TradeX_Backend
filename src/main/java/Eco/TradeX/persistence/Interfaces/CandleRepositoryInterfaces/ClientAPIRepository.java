package Eco.TradeX.persistence.Interfaces.CandleRepositoryInterfaces;

import Eco.TradeX.domain.CandleData;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.Instant;
import java.util.List;

public interface ClientAPIRepository {
    List<CandleData> getHistoricalCandles(Instant _from, Instant _to, String figi, CandleInterval interval);
    List<CandleData> getExtraHistoricalCandlesFromCertainTime(Instant _from, String figi, CandleInterval interval, int extraCandlesNeeded);
    List<CandleData> getExtraCandlesFromCertainTimeToFuture(Instant _from, String figi, CandleInterval interval, int extraCandlesNeeded);
}
