package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    Comment toComment(CommentRequestDto commentRequestDto);

    @Mapping(target = "post", ignore = true)
    @Mapping(target = "likes", ignore = true)
    Comment toComment(CommentResponseDto commentDto);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "likeCount", expression = "java(comment.getLikes().size())")
    CommentResponseDto toCommentDto(Comment comment);

    @Mapping(target = "postId", expression = "java(comment.getPost().getId())")
    @Mapping(target = "likes", source = "likeCount")
    @Mapping(target = "createdAt", expression = "java(formatLocalDateTime(comment.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(formatLocalDateTime(comment.getUpdatedAt()))")
    FeedCommentDto toFeedCommentDto(Comment comment);

    List<FeedCommentDto> toFeedCommentDtoList(List<Comment> comments);

    default String formatLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }
}

