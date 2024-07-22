package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.cache.entity.PostRedisCache;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {CommentFeedMapper.class, PostMapper.class, AuthorMapper.class})
public interface PostFeedMapper {

    @Mapping(source = "post", target = "post")
    @Mapping(target = "comments", ignore = true)
    PostFeedDto toDto(Post post, UserDto author);

    @Mapping(source = "post", target = "post")
    @Mapping(source = "comments", target = "comments")
    PostFeedDto toDto(Post post, UserDto author, List<CommentFeedDto> comments);

    @Mapping(source = "postRedisCache", target = "post", qualifiedByName = "buildPostDto")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "commentRedisCaches", target = "comments")
    PostFeedDto toDto(PostRedisCache postRedisCache);

    @Named("buildPostDto")
    default PostDto buildPostDto(PostRedisCache postRedisCache) {

        return PostDto.builder()
                .id(postRedisCache.getId())
                .content(postRedisCache.getContent())
                .publishedAt(postRedisCache.getPublishedAt())
                .createdAt(postRedisCache.getCreatedAt())
                .likesCount(postRedisCache.getLikesCount())
                .viewsCount(postRedisCache.getViewsCount())
                .build();
    }
}
