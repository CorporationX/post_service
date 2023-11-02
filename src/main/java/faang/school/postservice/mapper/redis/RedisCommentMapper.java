package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisCommentMapper {


    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesToDto")
    RedisCommentDto toRedisDto(Comment comment);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesDtosToDto")
    RedisCommentDto toRedisDto(CommentDto commentDto);

    @Named("mapLikesToDto")
    default Integer mapLikesToDto(List<Like> likes) {
        if (likes == null) {
            return 0;
        }
        return likes.size();
    }

    @Named("mapLikesDtosToDto")
    default Integer mapLikesDtosToDto(List<LikeDto> likes) {
        if (likes == null) {
            return 0;
        }
        return likes.size();
    }
}
