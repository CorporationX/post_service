package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostEventDto;

/**
 * Сервис управления лентами новостей.
 */
public interface FeedService {

    /**
     * Обновляет ленты новостей всех подписчиков при публикации нового поста.
     *
     * @param event событие поста с данными о посте и подписчиках
     */
    public void updateFeeds(PostEventDto event);
}
