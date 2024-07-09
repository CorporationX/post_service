package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "views", ignore = true)
    Post toEntity(PostDto postDto);

    PostDto toDto(Post post);

    List<PostDto> toListDto(List<Post> posts);

    @Mapping(target = "comments", ignore = true)
    PostRedis toRedis(Post post);

    List<PostDto> fromRedisToListDto(List<PostRedis> posts);
}