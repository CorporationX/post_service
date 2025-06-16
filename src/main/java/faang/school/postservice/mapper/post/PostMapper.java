package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostCreateProjectRequestDto;
import faang.school.postservice.dto.post.PostCreateUserRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.entity.post.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    Post toPostEntity(PostCreateUserRequestDto postCreateUserRequestDto);

    Post toPostEntity(PostCreateProjectRequestDto postCreateProjectRequestDto);

    @Mapping(target = "likeCount", expression = "java(mapLikeCount(post))")
    PostResponseDto toPostResponseDto(Post post);

    List<PostResponseDto> toPostResponseDtoList(List<Post> posts);

    void update(@MappingTarget Post post, PostUpdateRequestDto postUpdateRequestDto);

    default long mapLikeCount(Post post) {
        return post.getLikes() != null ? post.getLikes().size() : 0L;
    }
}
