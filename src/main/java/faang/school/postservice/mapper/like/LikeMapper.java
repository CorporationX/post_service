package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    LikeDto toLikeDto(Like like);

    @Mapping(source = "post.authorId", target = "postAuthorId")
    @Mapping(source = "userId", target = "likeAuthorId")
    @Mapping(source = "post.id", target = "postId")
    LikeEvent toLikeEventDto(Like like);
}
