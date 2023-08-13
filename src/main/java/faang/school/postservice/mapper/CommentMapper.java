package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "postId", target = "post", qualifiedByName = "mapPostIdToPost")
    Comment toEntity(CommentDto commentDto, @Context Long postId);

    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "postId", target = "post.id")
    Comment partialUpdate(CommentDto commentDto, @MappingTarget Comment comment);

    @Named("mapPostIdToPost")
    default Post mapPostIdToPost(Long postId) {
        return Post.builder().id(postId).build();
    }
}