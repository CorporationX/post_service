package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikePostEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeEventMapper {

    @Mapping(target = "localDateTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "authorPostId", source = "authorPostId")
    LikePostEvent mapLikePostEvent(LikeDto likeDto, long authorPostId);
}