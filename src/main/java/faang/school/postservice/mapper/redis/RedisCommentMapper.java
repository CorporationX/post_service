package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisCommentMapper {
    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesCommentToRedis")
    RedisCommentDto toRedisCommentDto(Comment comment);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapRedisToLikesComment")
    Comment toComment(RedisCommentDto commentDto);

    @Named("mapCommentsToRedisPost")
    default ArrayDeque<RedisCommentDto> mapCommentsToRedisPost(List<Comment> comments) {
        return comments == null ? new ArrayDeque<>(3) : comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .limit(3)
                .map(this::toRedisCommentDto)
                .collect(Collectors.toCollection(ArrayDeque::new));
    }

    @Named("mapRedisPostToComments")
    default List<Comment> mapRedisPostToComments(ArrayDeque<RedisCommentDto> comments) {
        return comments == null ? new ArrayList<>(3) : comments.stream()
                .map(this::toComment)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Named("mapLikesCommentToRedis")
    default Integer mapLikesCommentToRedis(List<Like> likes) {
        return likes.size();
    }

    @Named("mapRedisToLikesComment")
    default List<Like> mapRedisPostToLikes(int postLikes) {
        List<Like> likes = new ArrayList<>();
        for (int i = 0; i < postLikes; i++) {
            likes.add(new Like());
        }
        return likes;
    }
}
