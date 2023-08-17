package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.redis.LikeEventDto;
import faang.school.postservice.model.Like;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LikeEventMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "post.authorId")
    LikeEventDto toDto(Like like);

    Like toModel(LikeEventDto likeDto);
}
