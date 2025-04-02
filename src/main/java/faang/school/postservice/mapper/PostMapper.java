package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "likesCount", expression = "java(post.getLikes().size())")
    PostResponseDto toPostResponseDto(Post post);

    List<PostResponseDto> toPostResponseDtoList(List<Post> posts);

    Post toPost(PostRequestDto postRequestDto);
}
