package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentPostRequestDto;
import faang.school.postservice.dto.comment.CommentPostResponseDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    CommentPostResponseDto toPostResponseDto(CommentDto comment);

    CommentDto toDto(CommentPostRequestDto commentPostRequestDto);

    CommentDto toDto(Comment comment);

    Comment toEntity(CommentDto commentDto);
}