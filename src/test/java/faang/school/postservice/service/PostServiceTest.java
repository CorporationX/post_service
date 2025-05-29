package faang.school.postservice.service;

import faang.school.postservice.config.moderation.ModerationConfig;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostModerationAsyncHandler;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.PostValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    private static final long VALID_POST_ID = 1L;
    private static final long INVALID_POST_ID = 999L;
    private static final long VALID_PROJECT_ID = 1L;
    private static final long VALID_USER_ID = 1L;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostValidator postValidator;
    @Mock
    private PostModerationAsyncHandler postModerationAsyncHandler;
    @Mock
    private ModerationConfig postModerationConfig;

    @InjectMocks
    private PostService postService;

    private PostCreateDto postCreateDto;
    private PostViewDto postViewDto;
    private PostUpdateDto postUpdateDto;
    private Post post;
    private List<Post> posts;
    private final int batchesSize = 4;

    @BeforeEach
    public void setUp() {
        postCreateDto = new PostCreateDto();
        postViewDto = new PostViewDto();
        postUpdateDto = new PostUpdateDto();
        post = new Post();

        post.setContent("Test");
        posts = List.of(post);
    }

    @Test
    @DisplayName("Проверка успешного создания черновика")
    public void givenPostCreateDtoWhenCreateDraftThenReturnPostViewDto() {
        when(postMapper.createDtoToEntity(postCreateDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        PostViewDto result = postService.createDraft(postCreateDto);

        assertNotNull(result);

        verify(postMapper, Mockito.times(1)).createDtoToEntity(postCreateDto);
        verify(postRepository).save(post);
        verify(postMapper).toViewDto(post);
    }

    @Test
    @DisplayName("Проверка успешной публикации поста")
    public void givenVaLidPostIdWhenPublishPostThenReturnPostViewDto() {
        long postId = VALID_POST_ID;
        post.setPublished(false);

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));
        when(postMapper.toViewDto(post)).thenReturn(postViewDto);
        PostViewDto result = postService.publishPost(postId);

        assertNotNull(result);

        verify(postMapper, Mockito.times(1)).toViewDto(post);
        verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка ошибки при публикации несуществующего поста")
    public void givenNotExistPostIdWhenPublishPostThenReturnEntityNotFoundException() {
        long postId = INVALID_POST_ID;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> postService.publishPost(postId));

        Assertions.assertEquals(String.format("Post not found with id: %s", postId), exception.getMessage());
    }

    @Test
    @DisplayName("Проверка ошибки при публикации уже опубликованного поста")
    public void givenPublishedPostIdWhenPublishPostThenReturnDataValidationException() {
        long postId = VALID_POST_ID;
        post.setPublished(true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Exception exception = assertThrows(DataValidationException.class,
                () -> postService.publishPost(postId));

        Assertions.assertEquals("Post is already published", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка успешного обновления поста")
    public void givenValidPostUpdateDtoAndPostIdWhenUpdatePostThenReturnPostViewDto() {
        long postId = VALID_POST_ID;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        assertNotNull(postService.updatePost(postUpdateDto, postId));

        verify(postMapper, Mockito.times(1)).toViewDto(post);
        verify(postRepository, Mockito.times(1)).findById(postId);
        verify(postMapper, Mockito.times(1)).update(postUpdateDto, post);
    }

    @Test
    @DisplayName("Проверка ошибки при обновлении несуществующего поста")
    public void givenNotExistPostIdWhenUpdatePostThenReturnEntityNotFoundException() {
        long postId = INVALID_POST_ID;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> postService.updatePost(postUpdateDto, postId));

        Assertions.assertEquals(String.format("Post not found with id: %s", postId), exception.getMessage());
    }

    @Test
    @DisplayName("Проверка успешного мягкого удаления поста")
    public void givenValidPostIdWhenSoftDeletePostThenReturnPostViewDto() {
        long postId = VALID_POST_ID;
        post.setDeleted(false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        assertNotNull(postService.softDeletePost(postId));

        verify(postMapper, Mockito.times(1)).toViewDto(post);
        verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка ошибки при мягком удалении несуществующего поста")
    public void givenNotExistPostIdWhenSoftDeletePostThenReturnEntityNotFoundException() {
        long postId = INVALID_POST_ID;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> postService.softDeletePost(postId));

        Assertions.assertEquals(String.format("Post not found with id: %s", postId), exception.getMessage());

        verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка ошибки при мягком удалении уже удаленного поста")
    public void givenDeletedPostIdWhenSoftDeletePostThenReturnEntityDataValidationException() {
        long postId = VALID_POST_ID;
        post.setDeleted(true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Exception exception = assertThrows(DataValidationException.class,
                () -> postService.softDeletePost(postId));

        Assertions.assertEquals(String.format("Post with id %s is already deleted", postId), exception.getMessage());

        verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка успешного получения поста")
    public void givenValidPostIdWhenGetPostThenReturnPostViewDto() {
        long postId = VALID_POST_ID;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        PostViewDto result = postService.getPost(postId);

        assertNotNull(result);

        verify(postMapper, Mockito.times(1)).toViewDto(post);
        verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка ошибки при получении несуществующего поста")
    public void givenNotExistPostIdWhenGetPostThenReturnEntityNotFoundException() {
        long postId = INVALID_POST_ID;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> postService.getPost(postId));

        Assertions.assertEquals(String.format("Post not found with id: %s", postId), exception.getMessage());

        verify(postRepository, Mockito.times(1)).findById(postId);
    }

    @Test
    @DisplayName("Проверка успешного получения черновиков пользователя")
    public void givenValidUserIdWhenGetUserDraftsThenReturnListOfPostViewDto() {
        long userId = VALID_USER_ID;
        post.setDeleted(false);
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.now());

        when(postRepository.findByAuthorId(userId)).thenReturn(List.of(post));
        when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        assertNotNull(postService.getUserDrafts(userId));

        verify(postRepository, Mockito.times(1)).findByAuthorId(userId);
        verify(postMapper, Mockito.times(1)).toViewDto(post);
    }

    @Test
    @DisplayName("Проверка получения пустого списка черновиков пользователя")
    public void givenInvalidUserIdWhenGetUserDraftsThenReturnListOfPostViewDto() {
        long userId = VALID_USER_ID;

        when(postRepository.findByAuthorId(userId)).thenReturn(List.of());

        assertNotNull(postService.getUserDrafts(userId));

        verify(postRepository, Mockito.times(1)).findByAuthorId(userId);
    }

    @Test
    @DisplayName("Проверка успешного получения черновиков проекта")
    public void givenValidProjectIdWhenGetProjectDraftsThenReturnListOfPostViewDto() {
        long projectId = VALID_PROJECT_ID;
        post.setDeleted(false);
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.now());

        when(postRepository.findByProjectId(projectId)).thenReturn(List.of(post));
        when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        assertNotNull(postService.getProjectDrafts(projectId));

        verify(postRepository, Mockito.times(1)).findByProjectId(projectId);
        verify(postMapper, Mockito.times(1)).toViewDto(post);
    }

    @Test
    @DisplayName("Проверка получения пустого списка черновиков проекта")
    public void givenInvalidProjectIdWhenGetProjectDraftsThenReturnListOfPostViewDto() {
        long projectId = VALID_PROJECT_ID;

        when(postRepository.findByProjectId(projectId)).thenReturn(List.of());

        assertNotNull(postService.getProjectDrafts(projectId));

        verify(postRepository, Mockito.times(1)).findByProjectId(projectId);
    }

    @Test
    @DisplayName("Проверка успешного получения опубликованных постов пользователя")
    public void givenValidUserIdWhenGetAuthorPublishedPostThenReturnListOfPostsViewDto() {
        long userId = VALID_USER_ID;
        post.setDeleted(false);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        when(postRepository.findByAuthorIdWithLikes(userId)).thenReturn(List.of(post));
        when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        assertNotNull(postService.getAuthorPublishedPosts(userId));

        verify(postRepository, Mockito.times(1))
                .findByAuthorIdWithLikes(userId);
        verify(postMapper, Mockito.times(1))
                .toViewDto(post);
    }

    @Test
    @DisplayName("Проверка получения пустого списка опубликованных постов пользователя")
    public void givenInvalidUserIdWhenGetAuthorPublishedPostThenReturnListOfPostsViewDto() {
        long userId = VALID_USER_ID;
        post.setDeleted(false);
        post.setPublishedAt(LocalDateTime.now());

        when(postRepository.findByAuthorIdWithLikes(userId)).thenReturn(List.of());

        assertNotNull(postService.getAuthorPublishedPosts(userId));

        verify(postRepository, Mockito.times(1)).findByAuthorIdWithLikes(userId);
    }

    @Test
    @DisplayName("Проверка успешного получения опубликованных постов проекта")
    public void givenValidProjectIdWhenGetProjectPublishedPostThenReturnListOfPostsViewDto() {
        long projectId = VALID_PROJECT_ID;
        post.setDeleted(false);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(List.of(post));
        when(postMapper.toViewDto(post)).thenReturn(postViewDto);

        assertNotNull(postService.getProjectPublishedPosts(projectId));

        verify(postRepository, Mockito.times(1)).findByProjectIdWithLikes(projectId);
        verify(postMapper, Mockito.times(1)).toViewDto(post);
    }

    @Test
    @DisplayName("Проверка получения пустого списка опубликованных постов проекта")
    public void givenInvalidProjectIdWhenGetProjectPublishedPostThenReturnListOfPostsViewDto() {
        long projectId = VALID_PROJECT_ID;
        post.setDeleted(false);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(List.of());

        assertNotNull(postService.getProjectPublishedPosts(projectId));

        verify(postRepository, Mockito.times(1)).findByProjectIdWithLikes(projectId);
    }

    @Test
    @DisplayName("Проверка получения поста")
    public void givenExistingPostIdWhenGetPostEntityThenReturnPostEntity() {
        when(postRepository.findById(VALID_POST_ID))
                .thenReturn(Optional.of(post));

        Post returnedPost = postService.getPostEntity(VALID_POST_ID);

        assertNotNull(returnedPost);
    }

    @Test
    @DisplayName("Проверка получения поста, пост не найден")
    public void givenNonExistingPostIdWhenGetPostEntityThenThrowEntityNotFoundException() {
        when(postRepository.findById(VALID_POST_ID))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                postService.getPostEntity(VALID_POST_ID));

        Assertions.assertEquals("Post not found with id: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка модерации поста, пост промодерирован и прошел верификацию")
    public void givenValidDate_WhenModerateUnverifiedPost_ThenPostIsVerified() {
        when(postRepository.findAllByVerifiedAtIsNull()).thenReturn(posts);
        when(postModerationConfig.getBatchSize()).thenReturn(batchesSize);
        when(postModerationAsyncHandler.checkForProfanity(anyList()))
                .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> postService.moderateUnverifiedPost());
    }

    @Test
    @DisplayName("Проверка модерации поста, пост промодерирован и не прошел верификацию")
    public void givenValidDate_WhenModerateUnverifiedPost_ThenPostIsUnverified() {
        post.setContent("profanity");

        when(postRepository.findAllByVerifiedAtIsNull()).thenReturn(posts);
        when(postModerationConfig.getBatchSize()).thenReturn(batchesSize);
        when(postModerationAsyncHandler.checkForProfanity(anyList()))
                .thenReturn(CompletableFuture.completedFuture(null));


        assertDoesNotThrow(() -> postService.moderateUnverifiedPost());
    }

    @Test
    @DisplayName("Проверка модерации поста с пустым списком постов")
    public void givenEmptyPostList_WhenModerateUnverifiedPost_ThenNoModerationPerformed() {
        posts = Collections.emptyList();
        when(postRepository.findAllByVerifiedAtIsNull()).thenReturn(posts);

        assertDoesNotThrow(() -> postService.moderateUnverifiedPost());

        verify(postRepository).findAllByVerifiedAtIsNull();
    }
}