package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.model.Like;
import faang.school.postservice.protobuf.generate.LikePostEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikePostEventMapper {
    LikePostEventProto.LikePostEvent toProto(LikePostEvent likePostEvent);

    LikePostEvent toEvent(LikePostEventProto.LikePostEvent proto);

    @Mapping(target = "postId", source = "post.id")
    LikePostEvent toLikePostEventFromLike(Like like);
}
