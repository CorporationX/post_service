package faang.school.postservice.redis.mapper;

import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.redis.model.dto.CommentRedisDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentRedisMapper {
    CommentRedisDto toCommentRedisDto(Comment comment);
}
