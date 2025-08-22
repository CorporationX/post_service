package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;

import java.time.LocalDateTime;

public class PostControllerTestData {
    public static PostCreateDto buildCreateDto(Long authorId, Long projectId) {
        return new PostCreateDto(
                "Hello world",
                authorId,
                projectId
        );
    }

    public static Post buildPost(Long id, Long authorId, Long projectId, String content) {
        return Post.builder()
                .id(id)
                .authorId(authorId)
                .projectId(projectId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static PostViewDto toViewDto(Post post) {
        return new PostViewDto(
                post.getId(),
                post.getContent(),
                post.getAuthorId(),
                post.getProjectId(),
                post.isPublished(),
                post.isDeleted(),
                post.getPublishedAt(),
                post.getScheduledAt(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                null
        );
    }
}