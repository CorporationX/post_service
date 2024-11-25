package faang.school.postservice.mapper.redis;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostRedisMapper {
    @Mapping(target = "likeIds", source = "likes", qualifiedByName = "mapLikesToIds")
    @Mapping(target = "commentIds", source = "comments", qualifiedByName = "mapCommentsToIds")
    PostRedis postToPostRedis(Post post);

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post postRedisToPost(PostRedis postRedis);

    @Named("mapLikesToIds")
    default List<Long> mapLikesToIds(List<Like> likes) {
        if (likes == null) {
            return List.of();
        }
        return likes.stream().map(Like::getId).collect(Collectors.toList());
    }

    @Named("mapCommentsToIds")
    default List<Long> mapCommentsToIds(List<Comment> comments) {
        if (comments == null) {
            return List.of();
        }
        return comments.stream().map(Comment::getId).collect(Collectors.toList());
    }
}

