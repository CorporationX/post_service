package faang.school.postservice.filter;

import faang.school.postservice.dto.analytic.AnalyticsEventFilterDto;
import faang.school.postservice.model.analytic.AnalyticsEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class AnalyticsEventByIntervalFilter implements AnalyticsEventFilter {

    @Override
    public boolean isApplicable(AnalyticsEventFilterDto filter) {
        return filter.interval() != null;
    }

    @Override
    public Stream<AnalyticsEvent> apply(Stream<AnalyticsEvent> stream, AnalyticsEventFilterDto filter) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromInterval = now.minusSeconds(filter.interval().getSeconds());
        return stream
                .filter(analyticsEvent -> {
                    LocalDateTime created = analyticsEvent.getCreatedAt();
                    return created.isAfter(fromInterval) && created.isBefore(now);
                });
    }
}
