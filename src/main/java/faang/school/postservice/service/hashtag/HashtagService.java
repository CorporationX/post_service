package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.hashtag.HashtagViewDto;
import faang.school.postservice.dto.post.PostViewDto;

import java.util.List;

/**
 * HashtagService — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
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
