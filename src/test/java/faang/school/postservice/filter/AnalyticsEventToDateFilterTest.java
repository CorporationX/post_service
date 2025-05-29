package faang.school.postservice.filter;

import faang.school.postservice.dto.analytic.AnalyticsEventFilterDto;
import faang.school.postservice.model.analytic.AnalyticsEvent;
import faang.school.postservice.model.analytic.EventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AnalyticsEventToDateFilterTest {
    private final AnalyticsEventToDateFilter filter = new AnalyticsEventToDateFilter();

    @Test
    public void givenNotApplicableFilter_whenIsApplicable_thenReturnFalse() {
        AnalyticsEventFilterDto filterDto = new AnalyticsEventFilterDto(
                1L,
                2L,
                EventType.COMMENT_ADD,
                null,
                null,
                null);

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    public void givenApplicableFilter_whenIsApplicable_thenReturnTrue() {
        AnalyticsEventFilterDto filterDto = new AnalyticsEventFilterDto(
                1L,
                2L,
                EventType.COMMENT_ADD,
                null,
                null,
                LocalDateTime.now());

        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    public void givenFilterDto_whenApply_thenReturnResult() {
        LocalDateTime createdAt = LocalDateTime.now();

        AnalyticsEventFilterDto filterDto = new AnalyticsEventFilterDto(
                1L,
                2L,
                EventType.COMMENT_ADD,
                null,
                null,
                createdAt.plusHours(3)
        );

        Stream<AnalyticsEvent> stream = Stream.of(
                AnalyticsEvent.builder()
                        .id(1L)
                        .authorId(1L)
                        .receiverId(1L)
                        .eventType(EventType.COMMENT_ADD)
                        .createdAt(createdAt.minusHours(2))
                        .build(),
                AnalyticsEvent.builder()
                        .id(1L)
                        .authorId(1L)
                        .receiverId(1L)
                        .eventType(EventType.COMMENT_ADD)
                        .createdAt(createdAt.plusHours(14))
                        .build()
        );

        Stream<AnalyticsEvent> actualStream = filter.apply(stream, filterDto);

        assertEquals(1, actualStream.count());
    }
}
