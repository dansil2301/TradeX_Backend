package Eco.TradeX.business.Impl.StatisticsService;

import Eco.TradeX.business.Interfaces.StatisticsServiceInterfaces.GetStatisticsUseCase;
import Eco.TradeX.domain.Response.StatisticsResponse.GetStatisticsResponse;
import Eco.TradeX.domain.Statistics.Pages;
import Eco.TradeX.domain.Statistics.StatisticsItem;
import Eco.TradeX.persistence.Entities.PagesEntity;
import Eco.TradeX.persistence.Repositories.PagesRepository;
import Eco.TradeX.persistence.Repositories.PagesVisitedRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static Eco.TradeX.business.utils.StatisticsUtils.PagesConverter.covertToPages;

@Service
@AllArgsConstructor
public class GetStatisticsUseCaseImpl implements GetStatisticsUseCase {
    private final PagesRepository pagesRepository;
    private final PagesVisitedRepository pagesVisitedRepository;

    @Override
    public List<Pages> getPagesNames() {
        List<PagesEntity> pagesEntities = pagesRepository.findAll();
        return covertToPages(pagesEntities);
    }

    public GetStatisticsResponse getStatisticsPageVisited(Date from, Date to) {
        List<Object[]> statisticsResult = pagesVisitedRepository.findStatistics(from, to);

        List<StatisticsItem> statisticsItems = statisticsResult.stream()
                .map(this::mapToStatisticsItem)
                .collect(Collectors.toList());

        return GetStatisticsResponse.builder().statisticsPageVisited(statisticsItems).build();
    }

    public GetStatisticsResponse getStatisticsPageVisited(Date from, Date to, Pages pageName) {
        List<Object[]> statisticsResult = pagesVisitedRepository.findStatistics(from, to, pageName.toString());

        List<StatisticsItem> statisticsItems = statisticsResult.stream()
                .map(this::mapToStatisticsItem)
                .collect(Collectors.toList());

        return GetStatisticsResponse.builder().statisticsPageVisited(statisticsItems).build();
    }

    private StatisticsItem mapToStatisticsItem(Object[] result) {
        Long tradersVisited = ((Number) result[0]).longValue();
        Date visitedAt = (Date) result[1];

        return StatisticsItem.builder()
                .tradersVisited(tradersVisited)
                .visitedAt(visitedAt)
                .build();
    }
}
