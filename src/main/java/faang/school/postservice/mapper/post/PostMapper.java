package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.model.post.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    Post toPostEntity(final PostCreateRequestDto postCreateRequestDto);
    PostResponseDto toPostResponseDto(final Post post);

    List<PostResponseDto> toPostResponseDtoList(final List<Post> posts);

    void update(@MappingTarget Post post, final PostUpdateRequestDto postUpdateRequestDto);
}
