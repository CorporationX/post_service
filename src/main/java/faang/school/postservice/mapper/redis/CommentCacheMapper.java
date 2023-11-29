package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.cash.CommentCache;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentCacheMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentCache toCacheDto(Comment commentCache);

    @Mapping(source = "postId", target = "post.id")
    Comment toCacheEntity(CommentCache commentCache);
}
