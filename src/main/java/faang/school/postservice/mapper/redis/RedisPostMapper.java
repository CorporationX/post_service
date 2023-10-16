package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisPostMapper {

    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesToDto")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "mapCommentsToDto")
    RedisPost toRedisPost(Post post);

    @Named("mapLikesToDto")
    default Integer mapLikesToDto(List<Like> likes) {
        return likes.size();
    }

    @Named("mapCommentsToDto")
    default List<RedisCommentDto> mapCommentsToDto(List<Comment> comments) {
        RedisCommentMapper redisCommentMapper = new RedisCommentMapperImpl();
        if (comments == null) {
            return null;
        }
        List<RedisCommentDto> redisCommentDtos = new ArrayList<>();
        if (comments.size() > 3) {
            for (int i = comments.size() - 3; i < comments.size(); i++) {
                redisCommentDtos.add(redisCommentMapper.toDto(comments.get(i)));
            }
        } else {
            for (Comment comment : comments) {
                redisCommentDtos.add(redisCommentMapper.toDto(comment));
            }
        }
        return redisCommentDtos;
    }
}
