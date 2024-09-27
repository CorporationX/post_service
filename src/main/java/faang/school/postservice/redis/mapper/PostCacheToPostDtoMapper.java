package faang.school.postservice.redis.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.model.PostCache;
import faang.school.postservice.redis.repository.CommentCacheRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.redis.support.collections.RedisZSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheToPostDtoMapper {
    @Mapping(target = "comments", expression = "java(mapComments(postCache.getComments(), commentCacheRepository, commentCacheToCommentDtoMapper))")
    PostDto toDto(PostCache postCache, CommentCacheRepository commentCacheRepository, CommentCacheToCommentDtoMapper commentCacheToCommentDtoMapper);
    default List<CommentDto> mapComments(RedisZSet<Long> comments,
                                         CommentCacheRepository commentCacheRepository,
                                         CommentCacheToCommentDtoMapper commentCacheToCommentDtoMapper) {
        if (comments == null) {
            return new ArrayList<>();
        }
        return comments.stream()
                .map(id -> commentCacheRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(commentCacheToCommentDtoMapper::toDto)
                .toList();
    }
}