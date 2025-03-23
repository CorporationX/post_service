package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(target = "commentId", source = "like.comment.id")
    @Mapping(target = "postId", source = "like.post.id")
    LikeDto toDto(Like like);

    Like toEntity(LikeDto likeDto);

}
