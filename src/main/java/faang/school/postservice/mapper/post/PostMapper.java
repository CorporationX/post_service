package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.feed.FeedItemCommentDto;
import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.LinkedHashSet;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toPostEntity(PostCreateRequestDto postCreateRequestDto);

    Post toPostEntity(PostUpdateRequestDto postUpdateRequestDto);

    @Mapping(source = "published", target = "isPublished")
    PostResponseDto toPostResponseDto(Post post);

    List<PostResponseDto> toPostResponseDtos(List<Post> posts);

    PostDto toPostDto(Post post);

    @Mapping(target = "comments", source = "comments")
    PostResponseDto toPostResponseDto(PostResponseDto postResponseDto, LinkedHashSet<FeedItemCommentDto> comments);
}
