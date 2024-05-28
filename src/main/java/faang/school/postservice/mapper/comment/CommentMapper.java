package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "post.id", source = "postId")
    Comment toEntity(CreateCommentDto createCommentDto);

    @Mapping(target = "postId", source = "post.id")
    CreateCommentDto toDto(Comment comment);
}
