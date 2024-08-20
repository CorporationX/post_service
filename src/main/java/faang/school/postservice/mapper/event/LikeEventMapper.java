package faang.school.postservice.mapper.event;

import faang.school.postservice.event.like.LikeEvent;
import faang.school.postservice.dto.like.LikeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeEventMapper {

    @Mapping(target = "authorId", source = "userId")
    @Mapping(target = "likeId", source = "id")
    LikeEvent toLikeEvent(LikeDto likeDto);
}
