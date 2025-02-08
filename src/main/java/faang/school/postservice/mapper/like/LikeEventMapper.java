package faang.school.postservice.mapper.like;

import faang.school.postservice.model.Like;
import faang.school.postservice.model.event.LikeEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeEventMapper {

    @Mapping(target = "postId", expression = "java(like.getPost() != null ? like.getPost().getId() : null)")
    LikeEvent toEvent(Like like);
}
