package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface CommentMapper {
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(List<Comment> comments);

    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentDto commentDto);
}
