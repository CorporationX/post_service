package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostHashtagDto;
import faang.school.postservice.kafka.event.State;
import faang.school.postservice.kafka.event.post.PostEvent;
import faang.school.postservice.kafka.event.post.PostViewEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostLike;
import faang.school.postservice.redis.cache.entity.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntity(PostCreateDto postCreateDto);

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "getIdFromLike")
    PostHashtagDto toHashtagDto(Post post);

    @Mapping(source = "likeIds", target = "likes", qualifiedByName = "getLikeFromId")
    Post toEntity(PostHashtagDto post);

    @Mapping(source = "likeIds", target = "likesCount", qualifiedByName = "getCountFromList")
    PostDto toDto(PostHashtagDto post);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    PostDto toDto(Post post);

    @Mapping(source = "author.id", target = "authorId")
    PostDto toDto(PostCache post);

    @Mapping(source = "state", target = "state")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "post.authorId", target = "authorId")
    @Mapping(source = "post.createdAt", target = "createdAt")
    @Mapping(source = "post.publishedAt", target = "publishedAt")
    @Mapping(source = "post.content", target = "content")
    PostEvent toKafkaEvent(Post post, State state);

    @Mapping(source = "post.id", target = "postId")
    PostViewEvent toViewKafkaEvent(Post post);

    @Mapping(source = "postId", target = "id")
    @Mapping(source = "authorId", target = "author.id")
    PostCache toRedisCache(PostEvent post);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    PostCache toRedisCache(Post post);

    @Named("getCountFromLikeList")
    default long getCountFromLikeList(List<PostLike> likes) {
        return likes != null ? likes.size() : 0;
    }

    @Named("getCountFromList")
    default long getCountFromList(List<Long> ids) {
        return ids != null ? ids.size() : 0;
    }

    @Named("getIdFromLike")
    default long getIdFromLike(PostLike like) {
        return like != null ? like.getId() : 0;
    }

    @Named("getLikeFromId")
    default PostLike getLikeFromId(Long id) {
        return PostLike.builder().id(id).build();
    }
}
