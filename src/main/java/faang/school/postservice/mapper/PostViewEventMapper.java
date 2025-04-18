package faang.school.postservice.mapper;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.post.view.AnalyticsPostViewEvent;
import faang.school.postservice.model.event.post.view.NotificationPostViewEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PostViewEventMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "post.authorId")
    @Mapping(target = "viewerId", source = "viewerId")
    @Mapping(target = "timestamp", source = "viewedAt")
    AnalyticsPostViewEvent toAnalyticsEvent(Post post, Long viewerId, LocalDateTime viewedAt);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "post.authorId")
    @Mapping(target = "viewerId", source = "viewerId")
    @Mapping(target = "timestamp", source = "viewedAt")
    NotificationPostViewEvent toNotificationEvent(Post post, Long viewerId, LocalDateTime viewedAt);
}
