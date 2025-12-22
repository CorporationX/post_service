package faang.school.postservice.mapper;

import faang.school.postservice.dto.avro.CommentCreatedEventAvro;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.redis.UserRedisDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * CommentMapper — маппер из сущности {@link Comment} в dto и наоборот.
 *
 *
 * @author bozya
 * @since 21.08.2025
 */

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CommentMapper {

    public abstract Comment toEntity(CommentCreateDto createDto);

    @Mapping(expression = "java(comment.getPost().getId())", target = "postId")
    public abstract CommentViewDto toViewDto(Comment comment);

    public CommentCreatedEventAvro toAvro(Comment comment) {
        return CommentCreatedEventAvro.newBuilder()
            .setCommentId(comment.getId())
            .setPostId(comment.getPost().getId())
            .setAuthorId(comment.getAuthorId())
            .setContent(comment.getContent())
                .setCreatedAt(comment.getCreatedAt()
                        .atZone(ZoneId.of("Europe/Moscow"))
                        .toInstant())
            .build();
    }

    @Mapping(target = "id", source = "event.commentId")
    @Mapping(target = "author", source = "user")
    @Mapping(target = "content", source = "event.content")
    @Mapping(target = "createdAt", expression = "java(toLocalDateTime(event.getCreatedAt()))")
    public abstract CommentFeedDto toFeedDto(CommentCreatedEventAvro event, UserRedisDto user);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "author", source = "user")
    @Mapping(target = "content", source = "comment.content")
    @Mapping(target = "createdAt", expression = "java(comment.getCreatedAt())")
    public abstract CommentFeedDto toFeedDto(Comment comment, UserRedisDto user);

    protected LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}