package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentForListDto;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.CreateCommentResponse;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.dto.comment.UpdatedCommentResponse;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.event.CommentEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CreateCommentRequest commentRequest);

    CommentForListDto toListDto(Comment comment);

    @Mapping(source = "post.id", target = "postId")
    CreateCommentResponse toCreateResponse(Comment comment);

    @Mapping(source = "post.id", target = "postId")
    UpdatedCommentResponse toUpdateResponse(Comment comment);

    @Mapping(source = "authorId", target = "authorCommentId")
    @Mapping(source = "id", target = "commentId")
    CommentEvent toEvent(Comment comment);


    void updateComment(@MappingTarget Comment comment, UpdateCommentRequest updateCommentRequest);
}
