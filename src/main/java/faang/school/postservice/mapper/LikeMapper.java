package faang.school.postservice.mapper;

import faang.school.postservice.model.Like;
import faang.school.postservice.model.event.AnalyticsLikeEvent;
import faang.school.postservice.model.event.NotificationLikeEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "post.authorId", target = "postAuthorId")
    NotificationLikeEvent toNotificationLikeEvent(Like like);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "post.id", target = "postId")
    AnalyticsLikeEvent toAnalyticsLikeEvent(Like like);
}