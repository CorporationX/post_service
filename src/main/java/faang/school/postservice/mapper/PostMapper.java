package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    PostDto toDto(Post post);

    Post toPost(PostDto postDto);

    Post toPost(PostCreateDto postCreateDto);

    Post update(PostDto postDto);

    PostRedis toPostRedis(PostDto postDto);

    PostRedis toPostRedisFromEntity(Post post);
}
