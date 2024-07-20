package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostHashtagDto;
import faang.school.postservice.kafka.event.post.PostKafkaEvent;
import faang.school.postservice.kafka.event.post.PostViewKafkaEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostLike;
import faang.school.postservice.model.Resource;
import faang.school.postservice.redis.cache.entity.AuthorRedisCache;
import faang.school.postservice.redis.cache.entity.PostRedisCache;
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

    @Mapping(source = "post.id", target = "postId")
    PostKafkaEvent toKafkaEvent(Post post, List<Long> subscriberIds);

    @Mapping(source = "post.id", target = "postId")
    PostViewKafkaEvent toViewKafkaEvent(Post post);

    @Mapping(source = "authorId", target = "id")
    AuthorRedisCache toAuthorCache(Post post);

    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "getIdFromResource")
    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    @Mapping(source = "authorId", target = "author.id")
    PostRedisCache toRedisCache(Post post);

    @Named("getCountFromLikeList")
    default int getCountFromLikeList(List<PostLike> likes) {
        return likes != null ? likes.size() : 0;
    }

    @Named("getCountFromList")
    default int getCountFromList(List<Long> ids) {
        return ids != null ? ids.size() : 0;
    }

    @Named("getIdFromLike")
    default long getIdFromLike(PostLike like) {
        return like != null ? like.getId() : 0;
    }

    @Named("getIdFromResource")
    default long getIdFromResource(Resource resource) {
        return resource != null ? resource.getId() : 0;
    }

    @Named("getLikeFromId")
    default PostLike getLikeFromId(Long id) {
        return PostLike.builder().id(id).build();
    }
}
