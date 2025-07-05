package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.*;
import faang.school.postservice.model.Comment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    Comment toEntityFromCreateDto(CommentCreateDto dto);

    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CommentUpdateDto dto, @MappingTarget Comment comment);

    List<CommentDto> toDtoList(List<Comment> entities);
}
