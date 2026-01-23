package faang.school.postservice.mapper.feed;

import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.feed.AuthorDto;
import faang.school.postservice.dto.feed.FeedPostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

@Mapper(componentModel = "spring", nullValueCheckStrategy = ALWAYS)
public interface FeedPostMapper {

    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "content", source = "post.content")
    @Mapping(target = "published", source = "post.published")
    @Mapping(target = "deleted", source = "post.deleted")
    @Mapping(target = "createdAt", source = "post.createdAt")
    @Mapping(target = "updatedAt", source = "post.updatedAt")
    @Mapping(target = "projectId", source = "post.projectId")
    @Mapping(target = "author.id", source = "author.id")
    @Mapping(target = "author.username", source = "author.username")
    FeedPostResponseDto toDto(PostCacheDto post, UserDto author);

    /**
     * Safe wrapper:
     * - author may be null (cache miss / user-service miss)
     * - post is expected to be present (otherwise feed entry is skipped)
     */
    default FeedPostResponseDto toDtoSafe(PostCacheDto post, UserDto author) {
        if (post == null) {
            return null;
        }

        if (author == null) {
            return new FeedPostResponseDto(
                    post.id(),
                    post.content(),
                    post.published(),
                    post.deleted(),
                    post.createdAt(),
                    post.updatedAt(),
                    post.projectId(),
                    new AuthorDto(post.authorId(), null)
            );
        }

        return toDto(post, author);
    }
}
