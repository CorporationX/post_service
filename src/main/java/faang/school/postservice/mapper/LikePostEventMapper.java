package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.event_broker.LikePostEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikePostEventMapper {
    LikePostEvent toEvent(LikeDto likeDto);

    @Mapping(target = "postId", source = "post.id")
    LikePostEvent toEvent(Like like);
}
