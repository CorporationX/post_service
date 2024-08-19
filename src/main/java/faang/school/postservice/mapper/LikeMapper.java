package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.LikeEventV2;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "commentId", source = "comment.id")
    LikeDto toDto(Like like);

    @Mapping(target = "post.id", source = "postId")
    @Mapping(target = "comment.id", source = "commentId")
    Like toEntity(LikeDto likeDto);

    @Mapping(target = "likeAuthorId",source = "userId")
    @Mapping(target = "likedPostId", source = "postId")
    LikeEventV2 likeDtoToLikeEvent2(LikeDto dto);
}
