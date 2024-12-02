package faang.school.postservice.redis.mapper;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.redis.model.dto.CommentRedisDto;
import faang.school.postservice.redis.model.dto.PostRedisDto;
import faang.school.postservice.redis.model.entity.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.TreeSet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AuthorCacheMapper.class)
public interface PostCacheMapper {

    @Mapping(target = "comments", expression = "java(initializeComments())")
    PostCache toPostCache(PostDto postDto);

    PostRedisDto toPostRedisDto(PostCache postCache);

    PostRedisDto toPostRedisDto(Post post);

    default TreeSet<CommentRedisDto> initializeComments() {
        return new TreeSet<>();
    }
}
