package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.cash.PostCache;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CommentCacheMapper.class)
public interface PostCacheMapper {

    @Mapping(source = "likes", target = "likes", qualifiedByName = "mapToId")
    @Mapping(target = "id", expression = "java(String.valueOf(post.getId()))")
    PostCache toDto(Post post);

    @Mapping(source = "likes", target = "likes", qualifiedByName = "mapIdToLikes")
    @Mapping(target = "id", expression = "java(Integer.valueOf(postCache.getId()))")
    Post toEntity(PostCache postCache);

    @Named("mapToId")
    default List<Long> mapToId(List<Like> likes) {
        if (likes == null) {
            return Collections.emptyList();
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    @Named("mapIdToLikes")
    default List<Like> mapIdToLikes(List<Long> likes) {
        if (likes == null) {
            return Collections.emptyList();
        }
        return likes.stream()
                .map(id -> Like.builder().id(id).build())
                .toList();
    }
}
