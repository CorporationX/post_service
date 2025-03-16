package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDTO;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "albums", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "scheduledAt", ignore = true)
    Post toEntity(PostDTO postDTO);

    PostDTO toDto(Post post);
}
