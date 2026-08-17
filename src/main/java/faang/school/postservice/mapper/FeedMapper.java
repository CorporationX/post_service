package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.redis.UserRedisDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Маппер для получение DTO для feed
 *
 * @author Linempy
 * @since 28.09.2025
 */
@Component
public class FeedMapper {

    public PostFeedDto toFeedDto(PostRedisDto post, UserRedisDto user, List<CommentFeedDto> lastComments) {
        return new PostFeedDto(
                post.id(),
                post.content(),
                post.projectId(),
                user,
                lastComments,
                post.likeCount(),
                post.commentCount(),
                post.publishedAt()
        );
    }

    public List<PostFeedDto> toFeedDtos(List<PostRedisDto> posts,
                                         List<UserRedisDto> authors) {
        Map<Long, UserRedisDto> authorMap = authors.stream()
                .collect(Collectors.toMap(UserRedisDto::id, Function.identity()));

        return posts.stream()
                .map(post -> toFeedDto(post, authorMap.get(post.authorId()), null))
                .toList();
    }
}