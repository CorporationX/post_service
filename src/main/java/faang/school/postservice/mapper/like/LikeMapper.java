package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.entity.like.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    LikeResponseDto toLikeResponseDto(Like like);
}
