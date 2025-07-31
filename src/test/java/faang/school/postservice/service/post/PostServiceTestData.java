package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostServiceTestData {
    public static PostCreateDto buildCreateDto(Long authorId, Long projectId) {
        return new PostCreateDto("some content", authorId, projectId);
    }

    public static Post buildEntity(Long postId, Long authorId, Long projectId, LocalDateTime now) {
        Post.PostBuilder post = Post.builder();
        post.content("some content");
        post.id(postId);
        post.authorId(authorId);
        post.projectId(projectId);
        post.createdAt(now);
        post.updatedAt(now);
        return post.build();
    }

    public static Post toEntity(PostCreateDto createDto) {
        if (createDto == null) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        post.content(createDto.content());
        post.authorId(createDto.authorId());
        post.projectId(createDto.projectId());

        return post.build();
    }

    public static List<PostDto> toViewDtoList(List<Post> entities) {
        if (entities == null) {
            return null;
        }

        List<PostDto> list = new ArrayList<PostDto>(entities.size());
        for (Post post : entities) {
            list.add(toViewDto(post));
        }
        return list;
    }

    public static PostDto toViewDto(Post entity) {
        if (entity == null) {
            return null;
        }

        Long id = entity.getId();
        String content = entity.getContent();
        Long authorId = entity.getAuthorId();
        Long projectId = entity.getProjectId();
        boolean published = entity.isPublished();
        boolean deleted = entity.isDeleted();
        LocalDateTime publishedAt = entity.getPublishedAt();
        LocalDateTime scheduledAt = entity.getScheduledAt();
        LocalDateTime createdAt = entity.getCreatedAt();
        LocalDateTime updatedAt = entity.getUpdatedAt();
        return new PostDto(id, content, authorId, projectId, published, deleted, publishedAt, scheduledAt, createdAt, updatedAt);
    }
}
