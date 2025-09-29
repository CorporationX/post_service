package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.cache.FeedCommentCacheDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.CommentFeedEvent;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.comment.SaveCommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(SaveCommentDto saveCommentDto);

    List<CommentDto> toCommentDtos(List<Comment> comments);

    void update(SaveCommentDto saveCommentDto, @MappingTarget Comment comment);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "post.authorId", target = "postAuthorId")
    @Mapping(source = "id", target = "commentId")
    CommentEvent toCommentEvent(Comment comment);

    @Mapping(source = "commentId", target = "id")
    FeedCommentCacheDto toCacheEntry(CommentFeedEvent event);

    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "toInstantUtc")
    FeedCommentDto toFeedCommentDto(Comment comment);

    List<FeedCommentDto> toFeedCommentDtos(List<Comment> comments);

    @Named("toInstantUtc")
    static Instant toInstantUtc(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
