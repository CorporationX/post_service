package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "likesCount", expression = "java(post.getLikes().size())")
    PostResponseDto toPostResponseDto(Post post);

    List<PostResponseDto> toPostResponseDtoList(List<Post> posts);

    Post toPost(PostRequestDto postRequestDto);

    @Mapping(target = "likes", source = "likeCount")
    @Mapping(target = "comments", expression = "java(post.getComments().size())")
    @Mapping(target = "createdAt", expression = "java(formatLocalDateTime(post.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(formatLocalDateTime(post.getUpdatedAt()))")
    FeedPostDto toFeedPostDto(Post post);

    List<FeedPostDto> toFeedPostDtoList(List<Post> posts);

    default String formatLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }
}
