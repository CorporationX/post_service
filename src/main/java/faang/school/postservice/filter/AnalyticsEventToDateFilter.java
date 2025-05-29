package faang.school.postservice.filter;

import faang.school.postservice.dto.analytic.AnalyticsEventFilterDto;
import faang.school.postservice.model.analytic.AnalyticsEvent;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AnalyticsEventToDateFilter implements AnalyticsEventFilter {

    @Override
    public boolean isApplicable(AnalyticsEventFilterDto filter) {
        return filter.to() != null;
    }

    @Override
    public Stream<AnalyticsEvent> apply(Stream<AnalyticsEvent> stream, AnalyticsEventFilterDto filter) {
        return stream
                .filter(analyticsEvent -> analyticsEvent.getCreatedAt().isBefore(filter.to()));
    }
}
