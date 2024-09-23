package faang.school.postservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "albums", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "scheduledAt", ignore = true)
    Post toPost(PostDto postDto);

    @Mapping(target = "authorType", ignore = true)
    PostDto toPostDto(Post post);
}