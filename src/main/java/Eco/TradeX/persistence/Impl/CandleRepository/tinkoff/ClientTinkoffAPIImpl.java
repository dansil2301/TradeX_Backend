package Eco.TradeX.persistence.Impl.CandleRepository.tinkoff;

import Eco.TradeX.business.exceptions.CandlesExceptions;
import Eco.TradeX.domain.CandleData;
import com.google.protobuf.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import Eco.TradeX.persistence.Interfaces.CandleRepositoryInterfaces.ClientAPIRepository;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.InvestApi;

import java.time.*;
import java.util.Collections;
import java.util.List;

import static Eco.TradeX.business.utils.CandleUtils.CandleIntervalConverter.toSeconds;
import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

@Repository
public class ClientTinkoffAPIImpl implements ClientAPIRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAPIRepository.class);
    private final InvestApi investApi;

    public ClientTinkoffAPIImpl(InvestApi investApi){
        this.investApi = investApi;
    }

    @Override
    public List<CandleData> getHistoricalCandles(Instant _from, Instant _to, String figi, CandleInterval interval) {
        try {
            List<HistoricCandle> candles = investApi.getMarketDataService()
                    .getCandlesSync(figi, _from, _to, interval);

            if (candles.isEmpty()) {
                LOGGER.warn("Empty response for candles.");
                return null;
            }

            return candles.stream().map(originalCandle -> CandleData.builder()
                    .open(quotationToBigDecimal(originalCandle.getOpen()))
                    .close(quotationToBigDecimal(originalCandle.getClose()))
                    .high(quotationToBigDecimal(originalCandle.getHigh()))
                    .low(quotationToBigDecimal(originalCandle.getLow()))
                    .volume(originalCandle.getVolume())
                    .time(Instant.ofEpochSecond(originalCandle.getTime().getSeconds(), originalCandle.getTime().getNanos()))
                    .build()).toList();
        } catch (Exception e) {
            LOGGER.error("Error fetching historical candles: " + e.getLocalizedMessage());
            throw new CandlesExceptions(e.getMessage());
        }
    }

    private String getUidByFigi(String figi) {
        Instrument instrument = investApi.getInstrumentsService().getInstrumentByFigiSync(figi);
        return instrument.getUid();
    }

    public Instant getLastAvailableDate(String figi) {
        try {
            Instrument instrument = investApi.getInstrumentsService().getInstrumentByFigiSync(figi);
            Timestamp first1MinCandleTimestamp = instrument.getFirst1MinCandleDate();
            return Instant.ofEpochSecond(first1MinCandleTimestamp.getSeconds(), first1MinCandleTimestamp.getNanos());
        }
        catch (Exception e) {
            throw new CandlesExceptions(e.getMessage());
        }
    }

    private Boolean checkOpenOrClosedHolidays(Instant from) {
        Instant january9_2022 = Instant.parse("2022-01-09T00:00:00Z");
        if (from.isAfter(january9_2022)) {
            int hour = LocalDateTime.ofInstant(from, ZoneOffset.UTC).getHour();
            int minute = LocalDateTime.ofInstant(from, ZoneOffset.UTC).getMinute();

            // Check if 'from' is within the night period
            if ((hour >= 18 || (hour == 17 && minute >= 10)) || (hour <= 9 || (hour == 9 && minute <= 55))) {
                // 'from' is within the night period
                return true;
            }
        }
        return false;
    }

    public List<CandleData> getExtraHistoricalCandlesFromCertainTime(Instant _from, String figi, CandleInterval interval, int extraCandlesNeeded){
        Instant from = _from;
        List<CandleData> candles = Collections.emptyList();
        var stopDate = getLastAvailableDate(figi);

        while (candles.size() < extraCandlesNeeded) {
            int newPeriod = extraCandlesNeeded - candles.size();
            from = from.minusSeconds((long) toSeconds(interval) * newPeriod);

            ZonedDateTime zonedDateTime = from.atZone(ZoneOffset.UTC);
            int hourFrom = zonedDateTime.getHour(); int minuteFrom = zonedDateTime.getMinute();
            if (from.compareTo(stopDate) < 0) {
                break;
            }
            if (((hourFrom >= 22 || (hourFrom == 21 && minuteFrom >= 50)) || (hourFrom <= 7 || (hourFrom == 7 && minuteFrom <= 55)) ||
                    checkOpenOrClosedHolidays(from)) && (toSeconds(interval) < 86400)) {
                continue;
            }

            candles = getHistoricalCandles(from, _from, figi, interval);
            candles = (candles == null) ? Collections.emptyList() : candles;
        }

        if (extraCandlesNeeded != candles.size()) {
            candles = candles.subList(Math.max(candles.size() - extraCandlesNeeded, 0), candles.size());
        }

        return candles;
    }

    // todo build for future socket
    @Override
    public Candle getStreamServiceCandle(String figi, CandleInterval interval) {

        return null;
    }
}