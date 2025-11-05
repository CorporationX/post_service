package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostDto toPostDto(Post post);

    Post toPost(CreatePostDto createPostDto);
}
