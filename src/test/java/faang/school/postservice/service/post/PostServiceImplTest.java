package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PostServiceImplTest — тестирует сервис для постов {@link PostServiceImpl}
 *
 * @author Linempy
 * @since 28.07.2025
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование сервиса для постов")
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ProjectServiceClient projectClient;

    @Spy
    private PostMapper mapper;

    @Mock
    private UserContext context;

    @InjectMocks
    private PostServiceImpl service;

    @Test
    @DisplayName("Выбрасывает исключение, когда ID автора и ID проекта null")
    public void shouldThrowForbiddenException_WhenAuthorIdAndProjectIdAreNull() {
        PostCreateDto createDto = new PostCreateDto("content", null, null);

        Exception exception = assertThrows(DataValidationException.class, () -> service.create(createDto));
        assertEquals("Не указан идентификатор автора", exception.getMessage());
    }

    @Test
    @DisplayName("Выбрасывает исключение, когда ID автора и ID проекта не null")
    public void shouldThrowForbiddenException_WhenAuthorIdAndProjectIdAreNotNull() {
        PostCreateDto createDto = new PostCreateDto("content", 1L, 1L);
        Exception exception = assertThrows(ForbiddenException.class, () -> service.create(createDto));
        assertEquals("Должен быть указан только один идентификатор автора", exception.getMessage());
    }

    @Test
    @DisplayName("Выбрасывает исключение, если id автора не совпадает с id отправителем поста")
    public void shouldThrowForbiddenException_WhenAuthorIdNotEqualsCurrentUserid() {
        Long authorId = 1L;
        Long currentUserId = 2L;
        PostCreateDto createDto = new PostCreateDto("content", authorId, null);
        when(context.getUserId()).thenReturn(currentUserId);

        Exception exception = assertThrows(ForbiddenException.class, () -> service.create(createDto));
        assertEquals("Нельзя публиковать от чужого имени!", exception.getMessage());
    }

    @Test
    @DisplayName("Выбрасывает исключение при авторстве проекта, если пост пишет не участник проекта")
    public void shouldThrowForbiddenException_WhenProjectIdNotContainsCurrentUserId() {
        long projectId = 1L;
        Long currentUserId = 2L;
        ProjectDto project = new ProjectDto(projectId, "title", List.of(1L, 3L, 4L));
        PostCreateDto createDto = new PostCreateDto("content", null, projectId);

        when(context.getUserId()).thenReturn(currentUserId);
        when(projectClient.getProject(projectId)).thenReturn(project);

        Exception exception = assertThrows(ForbiddenException.class, () -> service.create(createDto));
        assertEquals("Вы не состоите в проекте!", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    @DisplayName("create должен вернуть PostViewDto при успешном создании поста")
    public void shouldReturnPostViewDto_WhenCreateSuccessful(
            Long userId, Long projectId, Long currentUserId, String content) {

        PostCreateDto createDto = new PostCreateDto(content, userId, projectId);
        Post expectedPost = Post.builder()
                .content(content)
                .authorId(userId)
                .projectId(projectId)
                .build();

        if (projectId != null) {
            ProjectDto project = new ProjectDto(projectId, "title", List.of(1L, 2L, 4L));
            when(projectClient.getProject(projectId)).thenReturn(project);
        }

        when(context.getUserId()).thenReturn(currentUserId);
        doReturn(expectedPost).when(mapper).toEntity(createDto);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(postCaptor.capture())).thenAnswer(inv -> {
            Post post = inv.getArgument(0);
            post.setId(1L);
            return post;
        });

        PostViewDto result = service.create(createDto);
        Post post = postCaptor.getValue();

        verify(mapper, times(1)).toViewDto(post);
        assertEquals(mapper.toViewDto(post), result);
        assertEquals(currentUserId, post.getAuthorId());
        assertEquals(content, post.getContent());
        assertEquals(projectId, post.getProjectId());
    }

    @Test
    @DisplayName("Выбрасываем исключение, если пост не был найден")
    public void shouldThrowEntityNotFound_WhenPostIsNotExist() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.publication(postId));
    }

    @Test
    @DisplayName("Выбрасывает исключение, если пост уже до этого был опубликован")
    public void shouldThrowForbidden_WhenPostIsPublishedAlready() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .published(true)
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));

        Exception exception = assertThrows(ForbiddenException.class, () -> service.publication(postId));
        assertEquals("Пост уже опубликован", exception.getMessage());
    }

    @Test
    @DisplayName("publication должен успешно поменять статус isPublished на true")
    public void shouldPublicationSuccessful() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .published(false)
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));

        service.publication(postId);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertTrue(savedPost.isPublished());
        assertNotNull(savedPost.getPublishedAt());
    }

    @Test
    @DisplayName("update должен успешно обновить пост и вернуть PostViewDto")
    public void shouldReturnPostViewDto_WhenUpdateSuccessful() {
        Long postId = 1L;
        PostUpdateDto updateDto = new PostUpdateDto("content");
        Post post = Post.builder()
                .id(postId)
                .content("Some text")
                .published(false)
                .build();
        Post updatedPost = Post.builder()
                .id(postId)
                .content(updateDto.content())
                .published(post.isPublished())
                .build();
        PostViewDto exceptedDto = new PostViewDto(
                updateDto.content(),
                post.getAuthorId(),
                post.getProjectId(),
                post.isPublished(),
                false,
                null);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(updatedPost);
        doReturn(exceptedDto).when(mapper).toViewDto(updatedPost);

        PostViewDto result = service.update(postId, updateDto);

        verify(mapper, times(1)).toViewDto(updatedPost);
        verify(mapper, times(1)).update(post, updateDto);
        verify(postRepository, times(1)).save(post);
        assertEquals(exceptedDto, result);
    }

    @Test
    @DisplayName("softDelete должен пометить пост как удаленный")
    public void shouldSoftDeleteSuccessful() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .published(false)
                .build();
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));

        service.softDelete(postId);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository, times(1)).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertTrue(savedPost.isDeleted());
        assertEquals(postId, savedPost.getId());
    }

    @Test
    @DisplayName("getById должен вернуть PostViewDto при успешном получении поста")
    public void shouldReturnPostViewDto_WhenGetByIdSuccessful() {
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .published(false)
                .build();
        PostViewDto expectedDto = new PostViewDto(
                post.getContent(),
                post.getAuthorId(),
                post.getProjectId(),
                post.isPublished(),
                post.isDeleted(),
                null
        );
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(mapper.toViewDto(post)).thenReturn(expectedDto);

        PostViewDto result = service.getById(postId);

        verify(postRepository, times(1)).findById(postId);
        verify(mapper, times(1)).toViewDto(post);
        assertEquals(expectedDto, result);
    }


    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(null, 1L, 2L, "content with project"),
                Arguments.of(1L, null, 1L, "content with user")
        );
    }


}