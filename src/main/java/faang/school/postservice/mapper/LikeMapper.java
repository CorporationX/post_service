package faang.school.postservice.mapper;

import faang.school.postservice.dto.avro.LikeCreateEventAvro;
import faang.school.postservice.dto.like.LikeCommentCreateDto;
import faang.school.postservice.dto.like.LikePostCreateDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.ZoneOffset;

/**
 * Маппер для преобразования {@link LikePostCreateDto} и
 * {@link LikeCommentCreateDto} в сущность {@link Like}
 *
 * @author Linempy
 * @since 12.12.2025
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "post", target = "post")
    Like toEntity(Long userId, Post post);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "comment", target = "comment")
    Like toEntity(Long userId, Comment comment);

    default LikeCreateEventAvro toAvro(Like like) {
        Long commentId = null;
        String content = like.getPost().getContent();
        if (like.getComment() != null) {
            commentId = like.getComment().getId();
            content = like.getComment().getContent();
        }

        return new LikeCreateEventAvro(
                like.getPost().getId(),
                commentId,
                like.getUserId(),
                content,
                like.getCreatedAt().atZone(ZoneOffset.UTC).toInstant()
        );
    }


}