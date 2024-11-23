package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "likeCount", source = "likeCount")
    PostResponseDto toResponseDto(Post post, int likeCount);

    PostResponseDto toResponseDto(Post post);

    Post toEntity(PostRequestDto postRequestDto);

    @Mapping(target = "likes", expression = "java(post.getLikes() != null ? (long) post.getLikes().size() : 0)")
    PostCacheDto toPostCacheDto(Post post);
}
