package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.hashtag.HashtagViewDto;
import faang.school.postservice.dto.post.PostViewDto;

import java.util.List;

/**
 * Сервис для управления хэштегами и их связью с постами.
 * <p>
 * Определяет операции для создания, обновления, удаления и получения постов по хэштегам,
 * а также для получения списка популярных хэштегов.
 * </p>
 *
 * @author Myrza
 * @since 08.08.2025
 */
public interface HashtagService {
    void create(PostViewDto post);

    void update(PostViewDto oldPost, PostViewDto newPost);

    void delete(PostViewDto post);

    List<PostViewDto> getList(String hashtagName);

    List<HashtagViewDto> getPopularHashtags(long offset, long limit);
}
