package faang.school.postservice.mapper.comment;

import faang.school.postservice.model.dto.comment.CommentRequestDto;
import faang.school.postservice.model.dto.comment.CommentResponseDto;
import faang.school.postservice.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "postId", source = "post.id")
    CommentResponseDto toResponseDto(Comment comment);

    @Mapping(target = "post.id", source = "postId")
    Comment toEntity(CommentRequestDto dto);

    List<CommentResponseDto> toResponseDto(List<Comment> comments);
}