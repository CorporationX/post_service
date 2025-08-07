package faang.school.postservice.cache.service;

import faang.school.postservice.dto.post.PostViewDto;

import java.util.List;

/**
 * PostCacheService — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Myrza
 * @since 07.08.2025
 */
public interface PostCacheService {
    void addPost(String hashtag, PostViewDto post);

    void deletePost(String hashtag, PostViewDto post);

    List<String> getPopularHashtags(long offset, long limit);

    List<PostViewDto> getList(String hashtag);
}
