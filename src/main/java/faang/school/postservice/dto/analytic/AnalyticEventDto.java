package faang.school.postservice.dto.analytic;

import faang.school.postservice.model.analytic.EventType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Полученная аналитика из БД для дальнейшей обработки
 * @param authorId Автор события
 * @param receiverId Получатель события eg: Автор, Пост
 * @param eventType Тип события
 * @param createdAt Когда событие произошло
 */
public record AnalyticEventDto(
        @NotNull Long authorId,
        @NotNull Long receiverId,
        @NotNull EventType eventType,
        @NotNull LocalDateTime createdAt
) {
}
