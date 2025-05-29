package faang.school.postservice.mapper;

import faang.school.postservice.dto.analytic.AnalyticEventDto;
import faang.school.postservice.dto.analytic.AnalyticsEventCreateDto;
import faang.school.postservice.model.analytic.AnalyticsEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnalyticsEventMapper {

    AnalyticsEvent toEntity(AnalyticsEventCreateDto analyticsEventCreateDto);

    AnalyticEventDto toAnalyticEventDto(AnalyticsEvent analyticsEvent);
}
