package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.redis.LikeRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LikeMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "comment.id", target = "commentId")
    LikeDto toDto(Like like);

    @Mapping(target = "post", ignore = true)
    @Mapping(target = "comment", ignore = true)
    Like toEntity(LikeDto likeDto);


    LikeRedis toRedis(Like like);
}
