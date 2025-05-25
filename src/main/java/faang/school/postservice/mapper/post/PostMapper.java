package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.model.Post;

public class PostMapper {

    public static Post PostDtoToPost(CreatePostDto createPostDto) {
        return Post.builder()
                .authorId(createPostDto.getAuthorId())
                .projectId(createPostDto.getProjectId())
                .content(createPostDto.getContent())
                .build();
    }

    public static CreatePostDto PostToCreatePostDto(Post post) {
        return CreatePostDto.builder()
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .content(post.getContent())
                .build();
    }

    public static Post PostDtoToPost(ResponsePostDto responsePostDto) {
        return Post.builder()
                .id(responsePostDto.getPostId())
                .authorId(responsePostDto.getAuthorId())
                .projectId(responsePostDto.getProjectId())
                .content(responsePostDto.getContent())
                .scheduledAt(responsePostDto.getScheduledAt())
                .build();
    }

    public static ResponsePostDto PostToResponsePostDto(Post post) {
        return ResponsePostDto.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .content(post.getContent())
                .scheduledAt(post.getScheduledAt())
                .build();
    }

    public static Post PostDtoToPost(UpdatePostDto updatePostDto) {
        return Post.builder()
                .content(updatePostDto.getContent())
                .scheduledAt(updatePostDto.getScheduledAt())
                .build();
    }

    public static UpdatePostDto PostToUpdatePostDto(Post post) {
        return UpdatePostDto.builder()
                .content(post.getContent())
                .scheduledAt(post.getScheduledAt())
                .build();
    }
}
