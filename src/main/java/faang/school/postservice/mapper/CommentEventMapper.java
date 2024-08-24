package faang.school.postservice.mapper;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper {

    @Mapping(source = "content", target = "comment")
    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "post", target = "postId", qualifiedByName = "fromPostToPostId")
    CommentEvent toCommentEvent(Comment comment);

    @Named("fromPostToPostId")
    default long fromPostToPostId(Post post) {
        return post.getId();
    }

}
