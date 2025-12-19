package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.request.RequestCreateComment;
import faang.school.postservice.dto.comment.response.ResponseComment;
import faang.school.postservice.model.Comment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING
)
public interface CommentMapper {

    @Mapping(target = "post", ignore = true)
    Comment toEntity(RequestCreateComment commentDto);

    @Mapping(target = "postId", source = "post.id")
    ResponseComment toDto(Comment comment);
}