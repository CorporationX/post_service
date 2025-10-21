package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.SendCommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(SendCommentDto sendCommentDto);


    @Mapping(target = "postId", source = "post.id")
    ResponseCommentDto toDto(Comment comment);

}