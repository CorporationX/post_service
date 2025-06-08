package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toEntity(CreatePostDto createPostDto);

    @Mapping(target = "resourcesIds",
            expression = "java(post.getResources() != null ? post.getResources().stream()" +
                    ".map(r -> r.getId()).toList() : null)")

    @Mapping(target = "likesIds",
            expression = "java(post.getLikes() != null ? post.getLikes().stream()" +
                    ".map(l -> l.getId()).toList() : null)")
    PostDto toPostDto(Post post);

    List<PostDto> toListPostDto(List<Post> posts);
}
