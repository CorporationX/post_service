package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {PostMapperHelper.class, CommentMapper.class})
public interface PostMapper {

    PostDto toDto(Post post);

    Post toEntity(PostDto post);

    List<PostDto> toDtoList(List<Post> posts);

    @Mapping(target = "commentSetId", ignore = true)
    PostRedis toRedisEntity(PostDto dto);





}
