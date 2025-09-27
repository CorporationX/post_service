package faang.school.postservice.mapper;

import faang.school.postservice.dto.avro.CommentCreatedEventAvro;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.ZoneId;

/**
 * CommentMapper — маппер из сущности {@link Comment} в dto и наоборот.
 *
 *
 * @author bozya
 * @since 21.08.2025
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    Comment toEntity(CommentCreateDto createDto);

    CommentViewDto toViewDto(Comment comment);

    default CommentCreatedEventAvro toAvro(Comment comment) {
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
}