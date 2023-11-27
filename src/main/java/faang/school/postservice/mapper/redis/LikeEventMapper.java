package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.LikeEventDto;
import faang.school.postservice.model.Like;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LikeEventMapper {

    @Mapping(target = "receiverId", source = "post.id")
    @Mapping(target = "actorId", source = "userId")
    @Mapping(target = "receivedAt", source = "createdAt")
    LikeEventDto toDto(Like like);

    @Mapping(target = "userId", source = "actorId")
    @Mapping(target = "createdAt", source = "receivedAt")
    Like toModel(LikeEventDto likeDto);
}
