package business.Impl.StrategiesService.RSI;

import Eco.TradeX.TradeXApplication;
import Eco.TradeX.business.Impl.CandleService.GetCandlesAPIInformationUseCaseImpl;
import Eco.TradeX.business.Impl.StrategiesService.MA.MAParameterContainer;
import Eco.TradeX.business.Impl.StrategiesService.MA.StrategyMAUseCaseImpl;
import Eco.TradeX.business.Impl.StrategiesService.RSI.RSIParameterContainer;
import Eco.TradeX.business.Impl.StrategiesService.RSI.StrategyRSIUseCaseImpl;
import Eco.TradeX.business.Interfaces.StrategiesServiceinterfaces.ParameterContainer;
import Eco.TradeX.business.utils.CandleUtils.CandlesSeparationAndInitiation;
import Eco.TradeX.domain.CandleData;
import Eco.TradeX.persistence.Impl.CandleRepository.tinkoff.ClientTinkoffAPIImpl;
import TestConfigs.BaseTest;
import business.Impl.CreateCandlesDataFake;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TradeXApplication.class)
class StrategyRSIUseCaseImplTest extends BaseTest {
    private final CreateCandlesDataFake createCandlesDataFake = new CreateCandlesDataFake();

    @Mock
    private ClientTinkoffAPIImpl clientAPIRepositoryMock;

    @Mock
    private CandlesSeparationAndInitiation candlesSeparationAndInitiationMock;

    @Mock
    private GetCandlesAPIInformationUseCaseImpl clientMock;

    @InjectMocks
    private StrategyRSIUseCaseImpl strategyRSIUseCaseMock;

    @Test
    void getStrategyParametersForCandlesLongMAMock() {
        Instant to = LocalDate.of(2023, 1, 2).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant from = to.minus(Duration.ofDays(1));

        List<CandleData> mockCandles = createCandlesDataFake.createCandles(31);
        when(clientMock.getHistoricalCandlesAPI(from, to, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN))
                .thenReturn(mockCandles);

        List<CandleData> candles = clientMock.getHistoricalCandlesAPI(from, to, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN);
        List<List<CandleData>> LstCandleData = new ArrayList<>();
        LstCandleData.add(mockCandles.subList(0, 21));
        LstCandleData.add(mockCandles);
        when(candlesSeparationAndInitiationMock.initiateCandlesProperly(candles, null, 21, from, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN))
                .thenReturn(LstCandleData);

        List<ParameterContainer> parameters = strategyRSIUseCaseMock.getStrategyParametersForCandles(candles, from, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN);
        assertEquals(candles.size(), parameters.size());
    }

    @Test
    void getStrategyParametersForCandles1MonthCandleLongMANotEnoughExtraCandlesCaseMock() {
        Instant to = LocalDate.of(2023, 1, 2).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant from = to.minus(Duration.ofDays(1));

        List<CandleData> mockCandles = createCandlesDataFake.createCandles(31);
        when(clientMock.getHistoricalCandlesAPI(from, to, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN))
                .thenReturn(mockCandles);

        List<CandleData> candles = clientMock.getHistoricalCandlesAPI(from, to, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN);
        List<List<CandleData>> LstCandleData = new ArrayList<>();
        LstCandleData.add(mockCandles.subList(0, 21));
        LstCandleData.add(mockCandles.subList(21, 30));
        when(candlesSeparationAndInitiationMock.initiateCandlesProperly(candles, null, 21, from, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN))
                .thenReturn(LstCandleData);

        List<ParameterContainer> parameters = strategyRSIUseCaseMock.getStrategyParametersForCandles(candles, from, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN);
        var parameterNull = (RSIParameterContainer)parameters.get(21);
        var parameterNotNull = (RSIParameterContainer)parameters.get(22);
        assertEquals(candles.size(), parameters.size());
        assertNull(parameterNull.getRSI());
        assertNotNull(parameterNotNull.getRSI());
    }

    @Test
    void getStrategyParametersForCandlesCandle1DaysLongMANoCandlesCaseMock() {
        Instant to = LocalDate.of(2023, 1, 2).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant from = to.minus(Duration.ofDays(1));

        when(clientMock.getHistoricalCandlesAPI(from, to, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN))
                .thenReturn(null);

        List<CandleData> candles = clientMock.getHistoricalCandlesAPI(from, to, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> strategyRSIUseCaseMock.getStrategyParametersForCandles(candles, from, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN)
        );

        String expectedMessage = "500 INTERNAL_SERVER_ERROR \"Candles Error: No candles found for this period\"";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void initializeExtraCandlesThroughFactorySimpleTestMock() {
        Instant to = LocalDate.of(2023, 1, 2).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant from = to.minus(Duration.ofDays(1));

        List<CandleData> mockCandles = createCandlesDataFake.createCandles(31);
        when(clientMock.getHistoricalCandlesAPI(from, to, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN))
                .thenReturn(mockCandles);

        List<CandleData> candles = clientMock.getHistoricalCandlesAPI(from, to, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN);
        List<List<CandleData>> LstCandleData = new ArrayList<>();
        LstCandleData.add(mockCandles.subList(0, 21));
        strategyRSIUseCaseMock.initializeExtraCandlesThroughFactory(mockCandles.subList(0, 21));
        LstCandleData.add(mockCandles.subList(21, 30));
        when(candlesSeparationAndInitiationMock.initiateCandlesProperly(candles, mockCandles.subList(0, 21), 21, from, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN))
                .thenReturn(LstCandleData);

        List<ParameterContainer> parameters = strategyRSIUseCaseMock.getStrategyParametersForCandles(candles, from, "testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN);
        assertEquals(candles.size(), parameters.size());
    }

    @Test
    void calculateParametersForCandleTestMock() {
        List<CandleData> mockCandles = createCandlesDataFake.createCandles(22);

        when(clientAPIRepositoryMock.getExtraHistoricalCandlesFromCertainTime(any(Instant.class), eq("testFigi"), eq(CandleInterval.CANDLE_INTERVAL_1_MIN), eq(21)))
                .thenReturn(mockCandles.subList(0, 21));
        Instant now = Instant.now();
        strategyRSIUseCaseMock.initializeContainerForCandleLiveStreaming("testFigi", CandleInterval.CANDLE_INTERVAL_1_MIN, now);

        ParameterContainer parameter = strategyRSIUseCaseMock.calculateParametersForCandle(mockCandles.get(21), CandleInterval.CANDLE_INTERVAL_1_MIN);
        assertEquals(true, parameter != null);
    }
}