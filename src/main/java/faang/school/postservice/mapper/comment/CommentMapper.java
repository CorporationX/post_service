package faang.school.postservice.mapper.comment;


import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toComment(CreateCommentRequest createCommentRequest);

    Comment toComment(UpdateCommentRequest updateCommentRequest);

    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "authorId", target = "commentAuthorId")
    @Mapping(source = "comment.post.id", target = "postId")
    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "content", target = "commentText")
    CommentEventDto toCommentEventDto(Comment comment);
}
