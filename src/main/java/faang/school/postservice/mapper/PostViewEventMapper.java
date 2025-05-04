package faang.school.postservice.mapper;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.post.PostEventType;
import faang.school.postservice.model.event.post.PostViewedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PostViewEventMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "post.authorId")
    @Mapping(target = "viewerId", source = "viewerId")
    @Mapping(target = "timestamp", source = "viewedAt")
    @Mapping(target = "type", source = "type")
    PostViewedEvent toEvent(Post post, Long viewerId, LocalDateTime viewedAt, PostEventType type);
}
