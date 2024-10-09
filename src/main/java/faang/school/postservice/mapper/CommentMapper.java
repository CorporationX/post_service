package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.event.CommentAddedEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(source = "likes", target = "likesId", qualifiedByName = "likesToLikesId")
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    Comment toEntity(CommentDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    CommentAddedEvent toCommentEvent(Comment comment);

    @Named("likesToLikesId")
    default List<Long> likesToLikesId(List<Like> likes) {
        if (likes == null) return List.of();
        return likes.stream().map(Like::getId).toList();
    }
}
