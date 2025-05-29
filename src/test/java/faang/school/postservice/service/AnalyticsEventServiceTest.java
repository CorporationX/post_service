package faang.school.postservice.service;

import faang.school.postservice.dto.analytic.AnalyticsEventCreateDto;
import faang.school.postservice.dto.analytic.AnalyticEventDto;
import faang.school.postservice.dto.analytic.AnalyticsEventFilterDto;
import faang.school.postservice.filter.AnalyticsEventByIntervalFilter;
import faang.school.postservice.filter.AnalyticsEventFromDateFilter;
import faang.school.postservice.filter.AnalyticsEventToDateFilter;
import faang.school.postservice.mapper.AnalyticsEventMapper;
import faang.school.postservice.model.analytic.AnalyticsEvent;
import faang.school.postservice.model.analytic.EventType;
import faang.school.postservice.repository.AnalyticsEventRepository;
import faang.school.postservice.service.analytics.AnalyticsEventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalyticsEventServiceTest {
    @Mock
    private AnalyticsEventRepository analyticsEventRepository;
    @Spy
    private AnalyticsEventMapper analyticsEventMapper = Mappers.getMapper(AnalyticsEventMapper.class);
    @Spy
    private AnalyticsEventByIntervalFilter analyticsEventByIntervalFilter = new AnalyticsEventByIntervalFilter();
    @Spy
    private AnalyticsEventFromDateFilter analyticsEventFromDateFilter = new AnalyticsEventFromDateFilter();
    @Spy
    private AnalyticsEventToDateFilter analyticsEventToDateFilter = new AnalyticsEventToDateFilter();

    private AnalyticsEventServiceImpl analyticsEventService;

    @BeforeEach
    public void init() {
        this.analyticsEventService = new AnalyticsEventServiceImpl(
                analyticsEventRepository,
                analyticsEventMapper,
                List.of(analyticsEventByIntervalFilter,
                        analyticsEventFromDateFilter,
                        analyticsEventToDateFilter)
        );
    }

    @Test
    public void givenEventDto_whenSaveEvent_thenSuccess() {
        AnalyticsEventCreateDto dto = new AnalyticsEventCreateDto(
                1L,
                2L,
                EventType.COMMENT_ADD,
                LocalDateTime.now());

        analyticsEventService.saveEvent(dto);

        verify(analyticsEventRepository, times(1)).save(any(AnalyticsEvent.class));
        verify(analyticsEventMapper, times(1)).toEntity(dto);
    }

    @Test
    public void givenAnalyticsFilterDto_whenGetAnalytics_thenReturnAnalytics() {
        Stream<AnalyticsEvent> analyticsEventStream = Stream.of(
                AnalyticsEvent.builder()
                        .id(1L)
                        .authorId(1L)
                        .receiverId(1L)
                        .eventType(EventType.COMMENT_ADD)
                        .build(),
                AnalyticsEvent.builder()
                        .id(2L)
                        .authorId(1L)
                        .receiverId(1L)
                        .eventType(EventType.COMMENT_ADD)
                        .build()
        );

        when(analyticsEventRepository.findByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 1L))
                .thenReturn(analyticsEventStream);

        AnalyticsEventFilterDto filterDto = new AnalyticsEventFilterDto(
                1L, 1L, null,
                null, null, null);
        List<AnalyticEventDto> actualAnalytic = analyticsEventService.getAnalytics(filterDto);

        verify(analyticsEventByIntervalFilter, times(1)).isApplicable(filterDto);
        verify(analyticsEventFromDateFilter, times(1)).isApplicable(filterDto);
        verify(analyticsEventToDateFilter, times(1)).isApplicable(filterDto);
        verify(analyticsEventMapper, times(2)).toAnalyticEventDto(any());

        assertFalse(actualAnalytic.isEmpty());
        assertEquals(2, actualAnalytic.size());
    }
}
