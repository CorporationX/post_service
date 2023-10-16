package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisCommentMapper {

    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesToDto")
    RedisCommentDto toDto(Comment comment);

    @Named("mapLikesToDto")
    default Integer mapLikesToDto(List<Like> likes) {
        return likes.size();
    }
}
