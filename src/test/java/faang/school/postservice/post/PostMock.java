package faang.school.postservice.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;

import java.time.LocalDateTime;
import java.util.List;

public class PostMock {
    public static final LocalDateTime now = LocalDateTime.now();
    public static final long authorId = 1L;
    public static final long projectId = 1L;
    public static final long userId = 1L;
    public static final long postId = 1L;
    public static final String content = "content";
    public static final String newContent = "newContent";

    public static UserDto generateUserDto() {
        return UserDto.builder()
                .id(userId)
                .username("username")
                .email("email@test.com")
                .build();
    }

    public static ProjectDto generateProjectDto() {
        return new ProjectDto(projectId, "projectName");
    }

    public static Post generatePost(Long authorId, Long projectId, Boolean published, String content) {
        return Post.builder()
                .id(postId)
                .content(content)
                .authorId(authorId)
                .projectId(projectId)
                .published(published)
                .publishedAt(null)
                .scheduledAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static PostDto generatePostDto(Long authorId, Long projectId, Boolean published, String content) {
        return PostDto.builder()
                .id(1L)
                .content(content)
                .authorId(authorId)
                .projectId(projectId)
                .published(published)
                .publishedAt(now)
                .scheduledAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static List<Post> generateFilteredPosts(Long authorId, Long projectId, Boolean published) {
        return List.of(
                Post.builder()
                        .id(1L)
                        .content(content)
                        .authorId(authorId)
                        .projectId(projectId)
                        .published(published)
                        .publishedAt(now)
                        .scheduledAt(null)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                Post.builder()
                        .id(2L)
                        .content(content)
                        .authorId(authorId)
                        .projectId(projectId)
                        .published(published)
                        .publishedAt(now.minusDays(1))
                        .scheduledAt(null)
                        .createdAt(now.minusDays(1))
                        .updatedAt(now.minusDays(1))
                        .build()
        );
    }

    public static List<PostDto> generateFilteredPostsDto(Long authorId, Long projectId, Boolean published) {
        return List.of(
                PostDto.builder()
                        .id(1L)
                        .content(content)
                        .authorId(authorId)
                        .projectId(projectId)
                        .published(published)
                        .publishedAt(now)
                        .scheduledAt(null)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                PostDto.builder()
                        .id(2L)
                        .content(content)
                        .authorId(authorId)
                        .projectId(projectId)
                        .published(published)
                        .publishedAt(now.minusDays(1))
                        .scheduledAt(null)
                        .createdAt(now.minusDays(1))
                        .updatedAt(now.minusDays(1))
                        .build()
        );
    }
}
