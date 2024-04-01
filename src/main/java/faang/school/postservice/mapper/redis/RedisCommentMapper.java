package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.RedisComment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisCommentMapper {
    RedisComment toRedisComment(CommentDto commentDto);
}
