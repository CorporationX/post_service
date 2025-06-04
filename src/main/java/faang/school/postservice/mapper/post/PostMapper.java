package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.model.Post;

import java.util.List;

public class PostMapper {

    public static Post postUpdateDtoToPost(PostCreateDto postCreateDto) {
        return Post.builder()
                .projectId(postCreateDto.getProjectId())
                .content(postCreateDto.getContent())
                .build();
    }

    public static PostCreateDto postToCreatePostDto(Post post) {
        return PostCreateDto.builder()
                .projectId(post.getProjectId())
                .content(post.getContent())
                .build();
    }

    public static Post postResponseDtoToPost(ResponsePostDto responsePostDto) {
        return Post.builder()
                .id(responsePostDto.getPostId())
                .authorId(responsePostDto.getAuthorId())
                .projectId(responsePostDto.getProjectId())
                .content(responsePostDto.getContent())
                .scheduledAt(responsePostDto.getScheduledAt())
                .build();
    }

    public static ResponsePostDto postToResponsePostDto(Post post) {
        return ResponsePostDto.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .content(post.getContent())
                .scheduledAt(post.getScheduledAt())
                .build();
    }

    public static Post postUpdateDtoToPost(PostUpdateDto postUpdateDto) {
        return Post.builder()
                .content(postUpdateDto.getContent())
                .scheduledAt(postUpdateDto.getScheduledAt())
                .build();
    }

    public static PostUpdateDto postToUpdatePostDto(Post post) {
        return PostUpdateDto.builder()
                .content(post.getContent())
                .scheduledAt(post.getScheduledAt())
                .build();
    }

    public static List<ResponsePostDto> postListToResponsePostDtoList(List<Post> postList) {
        return postList.stream().map(PostMapper::postToResponsePostDto).toList();
    }
}
