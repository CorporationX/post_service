package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.event.PostCommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapIds")
    @Mapping(source = "post.id", target = "postId")
    ResponseCommentDto toResponseDto(Comment comment);

    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "postId", source = "post.id")
    PostCommentEvent toPostCommentEvent(Comment comment);

    @Named("mapIds")
    default List<Long> getLikeIds(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(likes.stream().map(Like::getId).toArray(Long[]::new));
    }
}
