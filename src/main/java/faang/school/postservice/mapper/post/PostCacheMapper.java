package faang.school.postservice.mapper.post;


import faang.school.postservice.dto.comment.CommentRedisDto;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.redis.model.entity.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.TreeSet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {

    @Mapping(target = "comments", expression = "java(initializeComments())")
    PostCache toPostCache(PostCacheDto postDto);

    default TreeSet<CommentRedisDto> initializeComments() {
        return new TreeSet<>();
    }
}
