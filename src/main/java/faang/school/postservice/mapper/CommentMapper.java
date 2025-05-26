package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity (CommentForCreationDto dto);

    @Mapping(target = "postId", source = "post.id")
    CommentOutputDto toDto (Comment comment);

    List<CommentOutputDto> toListDto (List<Comment> comments);

    Comment updateEntityFromDto(CommentForUpdateDto dto, @MappingTarget Comment entity);
}
