package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.cache.entity.PostCache;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {CommentFeedMapper.class, UserMapper.class, PostMapper.class, AuthorMapper.class})
public interface PostFeedMapper {

    @Mapping(source = "author", target = "author")
    @Mapping(source = "post.id", target = "id")
    @Mapping(source = "post.createdAt", target = "createdAt")
    @Mapping(source = "post.publishedAt", target = "publishedAt")
    @Mapping(source = "post.content", target = "content")
    @Mapping(source = "post.likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    @Mapping(source = "post.viewsCount", target = "viewsCount")
    PostFeedDto toDto(Post post, UserDto author);

    @Mapping(source = "author", target = "author")
    PostFeedDto toDto(PostCache postCache);
}
