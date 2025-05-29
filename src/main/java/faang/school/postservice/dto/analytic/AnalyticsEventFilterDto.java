package faang.school.postservice.dto.analytic;

import faang.school.postservice.model.analytic.EventType;
import faang.school.postservice.model.analytic.Interval;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Объект для поиска и фильтрации аналитики
 * @param authorId Автор события
 * @param receiverId Получатель события eg: Автор, Пост
 * @param eventType Тип события
 * @param interval С какого интервала по текущий момент искать события
 * @param from Событие создано начиная с
 * @param to Событие создано до
 */
public record AnalyticsEventFilterDto(
        @NotNull Long authorId,
        @NotNull Long receiverId,
        @NotNull EventType eventType,
        Interval interval,
        LocalDateTime from,
        LocalDateTime to
) {
}
