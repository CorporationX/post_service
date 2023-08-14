package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    public abstract CommentDto toDto(Comment comment);

    @Mapping(source = "postId", target = "post", qualifiedByName = "mapPostIdToPost")
    @Mapping(target = "likes", ignore = true)
    public abstract Comment toEntity(CommentDto commentDto);

    @Mapping(source = "postId", target = "post", qualifiedByName = "mapPostIdToPost")
    @Mapping(target = "likes", ignore = true)
    public abstract void update(CommentDto commentDto, @MappingTarget Comment comment);

    @Named("mapPostIdToPost")
    protected Post mapPostIdToPost(Long postId) {
        return Post.builder().id(postId).build();
    }
}