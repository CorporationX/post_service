package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentRequestMapper {
    Comment toComment(CommentRequestDto commentRequestDto);

    @Mapping(target = "postId", source = "post.id")
    CommentRequestDto toCommentDto(Comment comment);
}

