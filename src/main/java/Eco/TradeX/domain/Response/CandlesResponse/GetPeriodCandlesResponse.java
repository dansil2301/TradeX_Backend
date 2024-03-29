package Eco.TradeX.domain.Response.CandlesResponse;

import Eco.TradeX.domain.CandleData;
import lombok.Builder;
import lombok.Data;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class GetPeriodCandlesResponse {
    private Instant from;
    private Instant to;
    private String figi;
    private CandleInterval interval;
    private List<CandleData> candles;
}
