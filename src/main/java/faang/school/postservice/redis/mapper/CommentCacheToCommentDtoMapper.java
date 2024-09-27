package faang.school.postservice.redis.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.redis.model.CommentCache;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CommentCacheToCommentDtoMapper {
    CommentDto toDto(CommentCache commentCache);
}