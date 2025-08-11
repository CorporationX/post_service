package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toPost(CreatePostDto createPostDto);

    void update(UpdatePostDto updatePostDto, @MappingTarget Post entity);

    @Mapping(target = "numberOfLikes", expression = "java(post.getLikes().size())")
    PostDto toPostDto(Post post);

    List<PostDto> toPostDtoList(List<Post> posts);
}
