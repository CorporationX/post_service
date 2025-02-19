package faang.school.postservice.mapper;


import faang.school.event.NotificationLikeEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
//    @Mapping(source = "id", target = "likeId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "post.authorId", target = "authorId")
    NotificationLikeEvent toNotificationLikeEvent(Like like);
}