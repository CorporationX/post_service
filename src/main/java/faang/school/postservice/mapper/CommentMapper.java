package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "postId", target = "post.id")
    Comment partialUpdate(CommentDto commentDto, @MappingTarget Comment comment);
}