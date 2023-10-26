package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.client.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CommentMapper {
    @Mapping(source = "post.id", target = "postId")
    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesToDto")
    CommentDto toDto(Comment comment);
    @Mapping(source = "postId", target = "post", qualifiedByName = "mapPostIdToPost")
    Comment toEntity(CommentDto commentDto);

    @Named("mapPostIdToPost")
    default Post mapPostIdToPost(Long postId) {
        return Post.builder().id(postId).build();
    }

    @Named("mapLikesToDto")
    default List<LikeDto> mapLikesToDto(List<Like> likes) {
        LikeMapper likeMapper = new LikeMapperImpl();
        if (likes.isEmpty()) {
            return null;
        }
        return likes.stream()
                .map(likeMapper::toDto)
                .toList();
    }
}
