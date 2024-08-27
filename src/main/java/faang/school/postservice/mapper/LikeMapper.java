package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.event.PostLikeEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {


    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "comment.id", target = "commentId")
    LikeResponseDto toResponseDto(Like like);

    @Mapping(source = "like.userId", target = "actorId")
    @Mapping(source = "like.post.id", target = "postId")
    @Mapping(source = "like.post.authorId", target = "postAuthorId")
    PostLikeEvent toPostLikeEvent(Like like);
}
