package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentResponse;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    default Integer getLikesCount(Comment comment) {
        return comment.getLikes() == null ? 0 : comment.getLikes().size();
    }

    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CreateCommentRequest createCommentRequest);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(expression = "java(getLikesCount(comment))", target = "likeCount")
    CommentResponse toCommentResponse(Comment comment);
}