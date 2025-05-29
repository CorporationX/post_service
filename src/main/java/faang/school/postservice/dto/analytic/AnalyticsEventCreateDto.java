package faang.school.postservice.dto.analytic;

import faang.school.postservice.model.analytic.EventType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO для создания аналитики
 * @param authorId Автор события
 * @param receiverId Получатель события eg: Автор, Пост
 * @param eventType Тип события
 * @param createdAt Когда событие произошло
 */
public record AnalyticsEventCreateDto(
        @NotNull Long authorId,
        @NotNull Long receiverId,
        @NotNull EventType eventType,
        @NotNull LocalDateTime createdAt
) {
}
