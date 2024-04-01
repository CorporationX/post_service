package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "postId", target = "post")
    Comment toEntity(CommentDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "post.authorId", target = "receiverId")
    CommentEventDto toEvent(Comment comment);

    default Post mapToPost(Long postId) {
        if (postId == null) {
            return null;
        }
        return Post.builder()
                .id(postId)
                .build();
    }

}
