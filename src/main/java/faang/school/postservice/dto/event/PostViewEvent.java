package faang.school.postservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostViewEvent {

    /**
     * ID просмотренного поста
     */
    private Long postId;

    /**
     * ID пользователя, который просмотрел пост
     */
    private Long viewerId;

    /**
     * ID автора поста
     */
    private Long authorId;

    /**
     * ID проекта (может быть null)
     */
    private Long projectId;

    /**
     * Время просмотра
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime viewedAt;

    /**
     * IP адрес пользователя (для аналитики)
     */
    private String ipAddress;

    /**
     * User Agent (для аналитики)
     */
    private String userAgent;

    /**
     * Источник просмотра (web, mobile_app, api)
     */
    private String source;

    /**
     * Время создания события
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventTimestamp;

    /**
     * Версия события для совместимости
     */
    private String eventVersion = "1.0";

    /**
     * Сессия пользователя
     */
    private String sessionId;

    /**
     * Продолжительность просмотра в миллисекундах (опционально)
     */
    private Long viewDurationMs;
}