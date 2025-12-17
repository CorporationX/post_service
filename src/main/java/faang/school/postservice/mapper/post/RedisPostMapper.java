package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.Ad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RedisPostMapper {

    @Mapping(source = "likes", target = "likeIds")
    @Mapping(source = "comments", target = "commentIds")
    @Mapping(source = "albums", target = "albumIds")
    @Mapping(source = "ad", target = "adId")
    @Mapping(source = "resources", target = "resourceIds")
    RedisPostDto toRedisPostDto(Post post);

    default Long map(Like like) {
        return like == null ? null : like.getId();
    }

    default Long map(Comment comment) {
        return comment == null ? null : comment.getId();
    }

    default Long map(Album album) {
        return album == null ? null : album.getId();
    }

    default Long map(Ad ad) {
        return ad == null ? null : ad.getId();
    }

    default Long map(Resource resource) {
        return resource == null ? null : resource.getId();
    }
}
