package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.enums.PostStatus;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

import static faang.school.postservice.service.post.PostServiceImplTestData.POST_ID_1;
import static faang.school.postservice.service.post.PostServiceImplTestData.PROJECT_ID_1;
import static faang.school.postservice.service.post.PostServiceImplTestData.USER_ID_1;
import static faang.school.postservice.service.post.PostServiceImplTestData.buildExpectedPost;
import static faang.school.postservice.service.post.PostServiceImplTestData.buildPost;
import static faang.school.postservice.service.post.PostServiceImplTestData.mockProjectClients;
import static faang.school.postservice.service.post.PostServiceImplTestData.mockUserContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("Выбрасывает исключение, когда и ID автора, и ID проекта null")
    public void testErrorWhenIdsAreNull() {
        PostCreateDto createDto = PostServiceImplTestData.createDtoWithNoAuthor();

        Exception exception = assertThrows(DataValidationException.class, () -> service.create(createDto));
        assertEquals("Не указан идентификатор автора", exception.getMessage());
    }

    @Test
    @DisplayName("Выбрасывает исключение, когда и ID автора, и ID проекта не null")
    public void testErrorWhenBothIdsNotnull() {
        PostCreateDto createDto = PostServiceImplTestData.createDtoWithBothAuthors();

        Exception exception = assertThrows(ForbiddenException.class, () -> service.create(createDto));
        assertEquals("Должен быть указан только один идентификатор автора", exception.getMessage());
    }

    @Test
    @DisplayName("Выбрасывает исключение, если id автора не совпадает с id отправителем поста")
    public void testErrorWhenAuthorIdNotEqualsCurrentUserid() {
        PostCreateDto createDto = PostServiceImplTestData.createDtoWithAuthorUser(1L);
        PostServiceImplTestData.mockUserContext(context, 3L);

        Exception exception = assertThrows(ForbiddenException.class, () -> service.create(createDto));
        assertEquals("Нельзя публиковать от чужого имени!", exception.getMessage());
    }

    @Test
    @DisplayName("Выбрасывает исключение при авторстве проекта, если пост написан не участником проекта")
    public void testErrorWhenProjectIdNotContainsCurrentUserId() {
        PostCreateDto createDto = new PostCreateDto("content", null, PROJECT_ID_1);

        mockUserContext(context, 2L);
        mockProjectClients(projectClient, PROJECT_ID_1, 1L, 4L, 3L);

        Exception exception = assertThrows(ForbiddenException.class, () -> service.create(createDto));
        assertEquals("Вы не состоите в проекте!", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("faang.school.postservice.service.post.PostServiceImplTestData#provideCreateTestCases")
    @DisplayName("При успешном создании поста должен вернуть корректный PostViewDto")
    public void testCreatePostSuccess(
            Long userId, Long projectId, Long currentUserId, String content) {

        PostCreateDto createDto = new PostCreateDto(content, userId, projectId);
        Post expectedPost = buildExpectedPost(content, userId, projectId);

        mockProjectClients(projectClient, projectId, 1L, 2L, 4L);
        mockUserContext(context, currentUserId);
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
        assertThat(mapper.toViewDto(post))
                .isEqualTo(result);
        assertThat(post)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(Post.builder()
                        .authorId(currentUserId)
                        .content(content)
                        .projectId(projectId)
                        .build());
    }

    @Test
    @DisplayName("Выбрасываем исключение, если пост не был найден")
    public void testPostNotFound() {
        when(postRepository.findPostOrThrow(POST_ID_1))
                .thenThrow(new EntityNotFoundException("Пост не найден"));

        assertThrows(EntityNotFoundException.class, () -> service.publication(POST_ID_1));
    }

    @Test
    @DisplayName("Выбрасывает исключение, если пост уже до этого был опубликован")
    public void testPostAlreadyPublished() {
        Post post = buildPost(null, POST_ID_1, null, null, true);
        when(postRepository.findPostOrThrow(POST_ID_1)).thenReturn(post);

        Exception exception = assertThrows(ForbiddenException.class, () -> service.publication(POST_ID_1));
        assertEquals("Пост уже опубликован", exception.getMessage());
    }

    @Test
    @DisplayName("publication должен успешно поменять статус isPublished на true")
    public void testPublicationSuccessful() {
        Post post = buildPost(null, POST_ID_1, null, null, false);
        when(postRepository.findPostOrThrow(POST_ID_1)).thenReturn(post);

        service.publication(POST_ID_1);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertTrue(savedPost.isPublished());
        assertNotNull(savedPost.getPublishedAt());
    }

    @Test
    @DisplayName("update должен успешно обновить пост и вернуть корректный PostViewDto")
    public void testUpdateSuccess() {
        PostUpdateDto updateDto = new PostUpdateDto("content");
        Post post = buildPost("Some text", POST_ID_1, null, null, false);
        Post updatedPost = buildPost(updateDto.content(), POST_ID_1, null, null, post.isPublished());
        PostViewDto exceptedDto = new PostViewDto(
                updateDto.content(),
                post.getAuthorId(),
                post.getProjectId(),
                post.isPublished(),
                false,
                null
        );

        when(postRepository.findPostOrThrow(POST_ID_1)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(updatedPost);
        doReturn(exceptedDto).when(mapper).toViewDto(updatedPost);

        PostViewDto result = service.update(POST_ID_1, updateDto);

        assertEquals(exceptedDto, result);
        verify(mapper, times(1)).toViewDto(updatedPost);
        verify(mapper, times(1)).update(post, updateDto);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    @DisplayName("softDelete должен пометить пост как удаленный")
    public void testSoftDeleteSuccessful() {
        Post post = buildPost(null, POST_ID_1, null, null, false);
        when(postRepository.findPostOrThrow(POST_ID_1)).thenReturn(post);

        service.softDelete(POST_ID_1);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository, times(1)).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertTrue(savedPost.isDeleted());
        assertEquals(POST_ID_1, savedPost.getId());
    }

    @Test
    @DisplayName("getById должен вернуть корректный PostViewDto при успешном получении поста")
    public void testGetPostSuccess() {
        Post post = Post.builder()
                .id(POST_ID_1)
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

        when(postRepository.findPostOrThrow(POST_ID_1)).thenReturn(post);
        when(mapper.toViewDto(post)).thenReturn(expectedDto);

        PostViewDto result = service.getById(POST_ID_1);

        verify(postRepository, times(1)).findPostOrThrow(POST_ID_1);
        verify(mapper, times(1)).toViewDto(post);
        assertEquals(expectedDto, result);
    }

    @ParameterizedTest
    @MethodSource("faang.school.postservice.service.post.PostServiceImplTestData#provideAuthorFilterCases")
    @DisplayName("Фильтрация по автору")
    public void testFilterAuthor(Long userId,
                                 Long projectId,
                                 Long expectedAuthorId,
                                 Long expectedProjectId) {
        PostFilterDto filterDto = new PostFilterDto(userId, projectId, null, null);
        Pageable pageable = PageRequest.of(0, 20);

        Post post = Post.builder()
                .content("Content")
                .authorId(expectedAuthorId)
                .projectId(expectedProjectId)
                .build();
        when(postRepository.findByFilter(filterDto, pageable)).thenReturn(new PageImpl<>(List.of(post)));

        Page<PostViewDto> result = service.findByFilter(filterDto, pageable);

        assertEquals(1, result.getContent().size());
        verify(postRepository, times(1)).findByFilter(filterDto, pageable);
        verify(mapper).toViewDto(post);
    }

    @ParameterizedTest
    @MethodSource("faang.school.postservice.service.post.PostServiceImplTestData#provideStatusFilterCases")
    @DisplayName("Фильтрация по статусу")
    public void testFilterStatus(PostStatus status) {
        PostFilterDto filterDto = new PostFilterDto(USER_ID_1, null, status, null);
        Pageable pageable = PageRequest.of(0, 20);

        boolean expectedPublished = status == PostStatus.PUBLISHED;
        Post post = Post.builder()
                .content("Content")
                .authorId(USER_ID_1)
                .published(expectedPublished)
                .build();
        PostViewDto expectedDto = new PostViewDto(
                post.getContent(), null, null, expectedPublished, false, null
        );
        when(postRepository.findByFilter(filterDto, pageable)).thenReturn(new PageImpl<>(List.of(post)));
        doReturn(expectedDto).when(mapper).toViewDto(post);

        Page<PostViewDto> result = service.findByFilter(filterDto, pageable);

        assertEquals(expectedPublished, result.getContent().get(0).published());
        assertEquals(1, result.getContent().size());
        verify(postRepository, times(1)).findByFilter(filterDto, pageable);
        verify(mapper).toViewDto(post);
    }

    @ParameterizedTest
    @MethodSource("faang.school.postservice.service.post.PostServiceImplTestData#provideMixedFilterCases")
    @DisplayName("Комбинированная фильтрация: корректно обрабатывает сочетание author + status + includeDeleted")
    void testMixedFilters(
            Long userId,
            Long projectId,
            PostStatus status,
            boolean includeDeleted,
            List<Post> expectedPosts) {

        PostFilterDto filterDto = new PostFilterDto(userId, projectId, status, includeDeleted);
        Pageable pageable = PageRequest.of(0, 20);

        when(postRepository.findByFilter(filterDto, pageable)).thenReturn(new PageImpl<>(expectedPosts));

        when(mapper.toViewDto(any(Post.class))).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            return new PostViewDto(
                    null,
                    p.getAuthorId(),
                    p.getProjectId(),
                    p.isPublished(),
                    p.isDeleted(),
                    null
            );
        });

        Page<PostViewDto> result = service.findByFilter(filterDto, pageable);

        assertThat(result.getContent()).hasSize(expectedPosts.size());
        expectedPosts.forEach(expectedPost -> assertThat(result.getContent())
                .anyMatch(dto ->
                        Objects.equals(dto.authorId(), expectedPost.getAuthorId()) &&
                        Objects.equals(dto.projectId(), expectedPost.getProjectId()) &&
                        dto.published() == expectedPost.isPublished() &&
                        dto.deleted() == expectedPost.isDeleted()
                ));
        verify(postRepository, times(1)).findByFilter(filterDto, pageable);
    }
}