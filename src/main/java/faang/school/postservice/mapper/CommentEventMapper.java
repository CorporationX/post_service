package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentEventDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentEventMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", source = "commentId")
    @Mapping(target = "post", source = "postId", qualifiedByName = "mapPostIdToPost")
    @Mapping(target = "createdAt", source = "date")
    Comment toCommentEntity(CommentEventDto commentEventDto, @Context long postId);

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "date", source = "createdAt")
    CommentEventDto toCommentDto(Comment comment);

    @Named("mapPostIdToPost")
    default Post mapPostIdToPost(Long postId) {
        return Post.builder().id(postId).build();
    }
}