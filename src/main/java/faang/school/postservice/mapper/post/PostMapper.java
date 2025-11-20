package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {
    PostDto toPostDto(Post post);

    List<PostDto> toListPostDto(List<Post> posts);

    Post toPost(CreatePostDto createPostDto);

    Post toPost(PostDto postDto);

    List<Post> toListPost(List<PostDto> postDtos);

    void updatePostDto(UpdatePostDto updatePostDto, @MappingTarget Post post);
}
