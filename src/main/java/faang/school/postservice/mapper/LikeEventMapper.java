package faang.school.postservice.mapper;

import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeEventMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "post.authorId")
    @Mapping(target = "userId", source = "userId")
    LikeEvent toEvent(Like like);
}
