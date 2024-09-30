package faang.school.postservice.mapper.like;

import faang.school.postservice.event.redis.like.LikeEvent;
import faang.school.postservice.dto.like.LikeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeEventMapper {

    @Mapping(source = "userId", target = "authorId")
    @Mapping(source = "id", target = "likeId")
    LikeEvent toLikeEvent(LikeDto likeDto);
}