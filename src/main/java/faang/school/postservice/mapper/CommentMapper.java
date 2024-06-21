package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "post.id", source = "postId")
    Comment toEntity(CreateCommentDto createCommentDto);

    @Mapping(target = "postId", source = "post.id")
    CreateCommentDto toDto(Comment comment);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "commentId", source = "id")
    CommentEventDto toEventDto(Comment comment);
}
