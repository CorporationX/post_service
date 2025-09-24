package faang.school.postservice.mapper.feed;

import faang.school.postservice.dto.cache.AuthorCacheDto;
import faang.school.postservice.dto.cache.FeedCommentCacheDto;
import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.dto.user.feed.FeedAuthorDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedMapper {

    PostCacheDto toPostCacheEntry(Post post);
    List<PostCacheDto> toPostCacheEntryList(List<Post> posts);

    @Mapping(target = "publishedAt", source = "publishedAt", qualifiedByName = "toInstantUtc")
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "lastComments", ignore = true)
    FeedPostDto toFeedPostDto(PostCacheDto entry);

    FeedAuthorDto toFeedAuthorDto(AuthorCacheDto entry);

    FeedCommentDto toFeedCommentDto(FeedCommentCacheDto src);

    List<FeedCommentDto> toFeedCommentDtosFromCache(List<FeedCommentCacheDto> src);

    @Mappings({
            @Mapping(target = "id", source = "post.id"),
            @Mapping(target = "content", source = "post.content"),
            @Mapping(target = "publishedAt", source = "post.publishedAt"),
            @Mapping(target = "author", source = "author"),
            @Mapping(target = "lastComments", source = "post.lastComments")
    })
    FeedPostDto attachAuthor(FeedPostDto post, FeedAuthorDto author);

    @Mappings({
            @Mapping(target = "id", source = "post.id"),
            @Mapping(target = "content", source = "post.content"),
            @Mapping(target = "publishedAt", source = "post.publishedAt"),
            @Mapping(target = "author", source = "post.author"),
            @Mapping(target = "lastComments", source = "comments")
    })
    FeedPostDto attachComments(FeedPostDto post, List<FeedCommentDto> comments);

    List<Long> toAuthorIds(List<PostCacheDto> entries);

    default Long map(PostCacheDto dto) {
        return dto == null ? null : dto.authorId();
    }

    @Named("toInstantUtc")
    static Instant toInstantUtc(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
