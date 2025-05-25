package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity (CommentDto dto);

    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto (Comment comment);

    List<CommentDto> toListDto (List<Comment> comments);

    Comment updateEntityFromDto(CommentDto dto, @MappingTarget Comment entity);
}
