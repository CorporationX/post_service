package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    Comment toEntity(CommentDto commentDto);

    CommentDto toCommentDto(Comment comment);

    void update(@MappingTarget Comment comment, CommentDto commentDto);

    List<Comment> toEntitieList(List<CommentDto> commentDtoList);

    List<CommentDto> toCommentDtoList(List<Comment> comments);
}
