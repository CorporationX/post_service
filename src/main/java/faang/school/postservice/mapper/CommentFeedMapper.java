package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.redis.cache.entity.CommentRedisCache;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {CommentMapper.class, AuthorMapper.class})
public interface CommentFeedMapper {

    CommentFeedDto toDto(Comment comment, UserDto author);

    @Mapping(source = "cache", target = "comment", qualifiedByName = "buildCommentDto")
    @Mapping(source = "author", target = "author")
    CommentFeedDto toDto(CommentRedisCache cache);

    @Named("buildCommentDto")
    default CommentDto buildCommentDto(CommentRedisCache cache) {

        return CommentDto.builder()
                .id(cache.getId())
                .content(cache.getContent())
                .createdAt(cache.getCreatedAt())
                .createdAt(cache.getCreatedAt())
                .likesCount(cache.getLikesCount())
                .build();
    }
}
