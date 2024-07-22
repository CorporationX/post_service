package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.redis.cache.entity.CommentCache;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {CommentMapper.class, AuthorMapper.class, UserMapper.class})
public interface CommentFeedMapper {

    @Mapping(source = "author", target = "author")
    @Mapping(source = "comment.id", target = "id")
    @Mapping(source = "comment.post.id", target = "postId")
    @Mapping(source = "comment.createdAt", target = "createdAt")
    @Mapping(source = "comment.content", target = "content")
    @Mapping(source = "comment.likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    CommentFeedDto toDto(Comment comment, UserDto author);

    @Mapping(source = "author", target = "author")
    CommentFeedDto toDto(CommentCache cache);
}
