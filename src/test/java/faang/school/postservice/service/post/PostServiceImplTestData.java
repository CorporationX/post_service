package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.AuthorFilter;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.enums.AuthorType;
import faang.school.postservice.model.enums.PostStatus;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

/**
 * Класс тестовых данных для юнит-тестов {@link PostServiceImpl}.
 * Содержит предопределенные константы и методы для создания тестовых объектов.
 *
 * @author Linempy
 * @since 01.08.2025
 */
public class PostServiceImplTestData {
    public static final Long USER_ID_1 = 1L;
    public static final Long PROJECT_ID_1 = 1L;
    public static final Long POST_ID_1 = 1L;
    public static final String CONTENT = "Test content";

    public static PostCreateDto createDtoWithNoAuthor() {
        return new PostCreateDto("content", null, null);
    }

    public static PostCreateDto createDtoWithBothAuthors() {
        return new PostCreateDto("content", USER_ID_1, PROJECT_ID_1);
    }

    public static PostCreateDto createDtoWithAuthorUser(Long userId) {
        return new PostCreateDto("content", userId, null);
    }

    public static void mockUserContext(UserContext context, Long userId) {
        when(context.getUserId()).thenReturn(userId);
    }

    public static void mockProjectClients(ProjectServiceClient client, Long projectId, Long... memberIds) {
        if (projectId != null) {
            ProjectDto project = new ProjectDto(projectId, "Test Project", List.of(memberIds));
            when(client.getProject(projectId))
                    .thenReturn(project);
        }
    }


    public static Post buildExpectedPost(String content, Long userId, Long projectId) {
        return Post.builder()
                .content(content)
                .authorId(userId)
                .projectId(projectId)
                .build();
    }

    public static Post buildPost(String content, Long postId, Long userId, Long projectId, Boolean isPublished) {
        return Post.builder()
                .id(postId)
                .content(content)
                .authorId(userId)
                .projectId(projectId)
                .published(isPublished)
                .build();
    }

    public static Stream<Arguments> provideCreateTestCases() {
        return Stream.of(
                Arguments.of(USER_ID_1, null, 1L, CONTENT),
                Arguments.of(null, PROJECT_ID_1, 1L, CONTENT)
        );
    }

    public static Stream<Arguments> provideAuthorFilterCases() {
        return Stream.of(
                Arguments.of(new AuthorFilter(AuthorType.USER, 1L), 1L, null),
                Arguments.of(new AuthorFilter(AuthorType.PROJECT, 1L), null, 1L)
        );
    }

    public static Stream<Arguments> provideStatusFilterCases() {
        return Stream.of(
                Arguments.of(PostStatus.PUBLISHED),
                Arguments.of(PostStatus.DRAFT)
        );
    }

    public static Stream<Arguments> provideMixedFilterCases() {
        Post user1Published = Post.builder().authorId(1L).published(true).build();
        Post project2Published = Post.builder().projectId(2L).published(true).build();
        Post deletedPost = Post.builder().authorId(1L).published(true).deleted(true).build();

        return Stream.of(
                Arguments.of(
                        new AuthorFilter(AuthorType.USER, 1L),
                        PostStatus.PUBLISHED,
                        false,
                        List.of(user1Published)
                ),

                Arguments.of(
                        new AuthorFilter(AuthorType.PROJECT, 2L),
                        null,
                        false,
                        List.of(project2Published)
                ),

                Arguments.of(
                        null,
                        null,
                        true,
                        List.of(deletedPost)
                )
        );
    }


}