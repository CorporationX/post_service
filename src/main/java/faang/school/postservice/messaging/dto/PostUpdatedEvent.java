package faang.school.postservice.messaging.dto;

import faang.school.postservice.dto.post.PostViewDto;

/**
 * Событие обновления поста.
 * <p>
 * Используется для передачи информации о предыдущем и новом состоянии поста
 * между сервисами через Kafka или другие механизмы обмена сообщениями.
 * </p>
 *
 * @param oldPost предыдущее состояние поста
 * @param newPost новое состояние поста
 * @author Myrza
 * @since 09.08.2025
 */
public record PostUpdatedEvent(
        PostViewDto oldPost,
        PostViewDto newPost
) {
}
