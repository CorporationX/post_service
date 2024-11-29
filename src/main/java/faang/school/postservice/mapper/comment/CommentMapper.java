package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.CommentNewsFeedDto;
import faang.school.postservice.dto.comment.CommentNotificationEvent;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentMapper {
    Comment toEntity(CommentRequestDto commentRequestDto);

    Collection<CommentResponseDto> toDtos(Collection<Comment> comments);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "listOfLikesToIds")
    @Mapping(target = "postId", source = "post.id")
    CommentResponseDto toDto(Comment comment);

    @Mapping(target = "postId", source = "postId")
    @Mapping(target = "authorId", source = "savedComment.authorId")
    @Mapping(target = "commentId", source = "savedComment.id")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    CommentEvent toCommentEvent(Long postId, Comment savedComment);

    @Mapping(target = "postId", source = "postId")
    @Mapping(target = "commentId", source = "savedComment.id")
    @Mapping(target = "authorPostId", source = "postAuthorId")
    @Mapping(target = "authorCommentId", source = "savedComment.authorId")
    @Mapping(target = "content", source = "savedComment.content")
    CommentNotificationEvent toNotificationEvent(Long postId, Comment savedComment, Long postAuthorId);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "listOfLikesToIds")
    @Mapping(target = "postId", source = "post.id")
    CommentNewsFeedDto toNewsFeedDto(Comment comment);

    @Named("listOfLikesToIds")
    default List<Long> listOfLikesToIds(List<Like> likes) {
        if (likes == null) {
            return Collections.emptyList();
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }
}
