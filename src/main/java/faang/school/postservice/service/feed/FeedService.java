package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.PostFeedDto;

import java.util.List;

/**
 * Сервис для получения feed пользователя
 *
 * @author Linempy
 * @since 28.09.2025
 */
public interface FeedService {

    /**
     * Метод для получения feed пользователя.
     * При cache miss идет в БД, чтобы добрать feed пользователю
     *
     * @param lastPostId последний просмотренный id поста в feed
     * @return список сформированных постов для feed {@link PostFeedDto}
     */
    List<PostFeedDto> getFeed(Long lastPostId);
}