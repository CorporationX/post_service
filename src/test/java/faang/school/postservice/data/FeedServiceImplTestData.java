package faang.school.postservice.data;

import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.redis.UserRedisDto;
import faang.school.postservice.service.feed.FeedServiceImplTest;

import java.util.List;

/**
 * Тестовые данные для {@link FeedServiceImplTest}
 *
 * @author Linempy
 * @since 02.10.2025
 */
public class FeedServiceImplTestData {

    public static final Long USER_ID = 1L;
    public static final Long POST_ID_1 = 1L;
    public static final Long POST_ID_2 = 2L;
    public static final Long POST_ID_3 = 3L;

    public static List<Long> getPostIdsFromRedis() {
        return List.of(POST_ID_3, POST_ID_2, POST_ID_1);
    }

    public static List<Long> getPostIdsFromDb() {
        return List.of(POST_ID_1, POST_ID_2, POST_ID_3);
    }

    public static List<PostRedisDto> getPosts() {
        return List.of(
                new PostRedisDto(POST_ID_1, "Content 1", 100L, 1L, 10L, 5L, null),
                new PostRedisDto(POST_ID_2, "Content 2", 200L, 2L, 15L, 3L, null),
                new PostRedisDto(POST_ID_3, "Content 3", 300L, 1L, 20L, 8L, null)
        );
    }

    public static List<UserRedisDto> getAuthors() {
        return List.of(
                new UserRedisDto(1L, "user1"),
                new UserRedisDto(2L, "user2")
        );
    }

    public static List<PostFeedDto> getFeedDtos() {
        return List.of(
                new PostFeedDto(POST_ID_1, "Content 1", 100L, getAuthors().get(0), List.of(), 10L, 5L, null),
                new PostFeedDto(POST_ID_2, "Content 2", 200L, getAuthors().get(1), List.of(), 15L, 3L, null),
                new PostFeedDto(POST_ID_3, "Content 3", 300L, getAuthors().get(0), List.of(), 20L, 8L, null)
        );
    }
}