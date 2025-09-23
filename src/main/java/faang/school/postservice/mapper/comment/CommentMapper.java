package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.SaveCommentDto;
import faang.school.postservice.dto.feed.FeedCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.CommentCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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

    CommentCache toCommentCache(Comment comment);
    List<CommentCache> toCommentCacheList(List<Comment> commentList);

    FeedCommentDto toFeedCommentDto(Comment comment);
    FeedCommentDto toFeedCommentDto(CommentCache commentCache);
}
