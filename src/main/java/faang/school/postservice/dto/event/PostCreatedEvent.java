package faang.school.postservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreatedEvent {

    /**
     * ID созданного поста
     */
    private Long postId;

    /**
     * ID автора поста
     */
    private Long authorId;

    /**
     * Содержимое поста (может быть null для приватности)
     */
    private String content;

    /**
     * ID проекта (может быть null)
     */
    private Long projectId;

    /**
     * Опубликован ли пост
     */
    private boolean published;

    /**
     * Список ID всех подписчиков автора
     */
    private List<Long> followerIds;

    /**
     * Время создания поста
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Время отправки события
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventTimestamp;

    /**
     * Версия события для совместимости
     */
    private String eventVersion = "1.0";
}