package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.redisEvent.LikeEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeEventMapper {
    @Mapping(source = "postId" , target = "postId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "eventAt", expression = "java(java.time.LocalDateTime.now())")
    LikeEvent toEntity(LikeDto likeEventDto);
}
