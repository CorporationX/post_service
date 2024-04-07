package faang.school.postservice.mapper.redis;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.redis.RedisComment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RedisCommentMapper {

    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikes")
    RedisComment toRedisEntity(Comment comment);

    @Named("mapLikes")
    default long mapLikes(List<Like> likes){
        return (long) likes.size();
    }
}