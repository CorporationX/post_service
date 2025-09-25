package faang.school.postservice.mapper;

import faang.school.postservice.dto.event.PostNewEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = {CommentMapper.class},
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {
    @Mapping(source = "likes", target = "likes", qualifiedByName = "countLikes")
    PostDto toDto(Post post);

    PostDto toDto(PostRedis postRedis);

    @Mapping(target = "likes", ignore = true)
    Post toEntity(PostDto dto);

    @Mapping(source = "likes", target = "likes", qualifiedByName = "countLikes")
    @Mapping(target = "comments", ignore = true)
    PostRedis toPostRedis(Post post);

    PostRedis toPostRedis(PostDto post);

    @Mapping(source = "id", target = "postId")
    PostNewEvent toEvent(Post post);
}
