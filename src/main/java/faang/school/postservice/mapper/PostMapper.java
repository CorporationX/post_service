package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PostMapper {

    PostDto toDto(Post post);

    Post toPost(PostDto postDto);

    Post toPost(PostCreateDto postCreateDto);

    Post update(PostDto postDto);
}
