package faang.school.postservice.service.analytics;

import faang.school.postservice.dto.analytic.AnalyticsEventCreateDto;
import faang.school.postservice.dto.analytic.AnalyticEventDto;
import faang.school.postservice.dto.analytic.AnalyticsEventFilterDto;

import java.util.List;

public interface AnalyticsEventService {

    void saveEvent(AnalyticsEventCreateDto analyticsEventCreateDto);

    List<AnalyticEventDto> getAnalytics(AnalyticsEventFilterDto filter);
}
