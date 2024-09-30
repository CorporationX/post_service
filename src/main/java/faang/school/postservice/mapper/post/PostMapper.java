package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.redis.post.PostEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CommentMapper.class)
public interface PostMapper {

    Post toEntity(PostDto postDto);

    PostDto toDto(Post post);

    PostEvent toPostEvent(Post post);

    @Mapping(target = "likes", ignore = true)
    PostRedis toPostRedis(Post post);
}