package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toEntity(CreatePostDto createPostDto);
    PostDto toPostDto(Post post);
    List<PostDto> toListPostDto(List<Post> posts);
}
