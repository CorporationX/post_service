package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.dto.post.PostFeedDto;

import java.util.List;

/**
 * Сервис управления лентами новостей.
 */
public interface FeedService {

    /**
     * Обновляет ленты новостей всех подписчиков при публикации нового поста.
     *
     * @param event событие поста с данными о посте и подписчиках
     */
    void updateFeeds(PostEventDto event);

    /**
     * Возвращает фид пользователя с пагинацией.
     *
     * @param userId ID пользователя
     * @param afterId ID поста, после которого загружать следующие посты (опционально)
     * @param pageSize количество постов (опционально, по умолчанию 20)
     * @return список постов фида
     */
    List<PostFeedDto> getUserFeed(Long userId, Long afterId, Integer pageSize);
}
