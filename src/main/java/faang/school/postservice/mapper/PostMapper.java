package faang.school.postservice.mapper;

import faang.school.postservice.cache.model.CacheablePost;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.cache.model.CacheableUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "likesToLikesCount")
    PostDto toDto(Post entity);

    List<PostDto> toDto(List<Post> entities);

    Post toEntity(PostDto dto);

    @Mapping(target = "comments", ignore = true)
    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "likesToLikesCount")
    @Mapping(source = "authorId", target = "author", qualifiedByName = "authorIdToAuthor")
    CacheablePost toCacheable(Post entity);

    List<CacheablePost> toCacheable(List<Post> entities);

    @Named("likesToLikesCount")
    default long likesToLikesCount(List<Like> likes) {
        if (likes == null) {
            return 0L;
        }
        return (likes.size());
    }

    @Named("authorIdToAuthor")
    default CacheableUser authorIdToAuthor(Long authorId) {
        if (authorId == null) {
            return null;
        }
        return new CacheableUser(authorId, null);
    }
}