package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.redis.RedisPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisPostMapper {

    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesDtoToCountLikes")
    @Mapping(target = "userId", source = "authorId", qualifiedByName = "mapAuthorIdToUserId")
    RedisPost toEntity(PostDto postDto);

    @Named("mapLikesDtoToCountLikes")
    default int mapLikesDtoToCountLikes(List<LikeDto> likes) {
        if (likes == null) {
            return 0;
        }
        return likes.size();
    }

    @Named("mapAuthorIdToUserId")
    default Long mapAuthorIdToUserId(Long userId) {
        return userId;
    }
}
