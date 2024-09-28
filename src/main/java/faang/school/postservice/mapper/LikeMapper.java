package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    LikeDto toDto(Like like);

    Like toEntity(LikeDto likeDto);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "userId", target = "likeUserId")
    @Mapping(source = "post.authorId", target = "postAuthorId")
    LikeEvent toLikeEvent(Like like);
}
