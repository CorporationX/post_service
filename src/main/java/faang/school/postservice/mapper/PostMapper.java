package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PostMapper {

    PostDto toDto(Post post);

    Post toPost(PostDto postDto);

    Post toPost(PostCreateDto postCreateDto);

    @Mapping(source = "content", target = "content")
    Post update(PostDto postDto);
}
