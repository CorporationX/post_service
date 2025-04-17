package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.async.AsyncConfig;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.dto.post.ReadPostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.redis.PostEventPublisher;
import faang.school.postservice.publisher.kafka.PostEventProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.corrector.PostCorrector;
import faang.school.postservice.service.moderate.ModerationService;
import faang.school.postservice.validator.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    private static final long ID = 1L;
    private static final long AUTHOR_ID = 2L;
    private static final long PROJECT_ID = 10L;
    private static final String TEST_CONTENT = "Тестовая запись";
    private static final String UPDATE_CONTENT = "Обновленная запись";
    private static final String EXCEPTION_MESSAGE = "Пост уже опубликован и не может быть опубликован повторно";

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostValidator postValidator;

    @Mock
    private PostCorrector postCorrector;

    @Mock
    private ModerationService moderationService;

    @Mock
    private AsyncConfig asyncConfig;

    @Mock
    private PostEventPublisher postEventPublisher;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostEventProducer postEventProducer;

    private final Executor executor = Executors.newSingleThreadExecutor();

    @InjectMocks
    private PostService postService;

    private Post post;
    private ReadPostDto readPostDto;
    private ReadPostDto expectedResponse;
    private CreatePostDto createPostDto;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(ID)
                .authorId(AUTHOR_ID)
                .projectId(PROJECT_ID)
                .content(TEST_CONTENT)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        readPostDto = ReadPostDto.builder()
                .id(ID)
                .authorId(AUTHOR_ID)
                .projectId(PROJECT_ID)
                .content(TEST_CONTENT)
                .published(false)
                .deleted(false)
                .build();

        createPostDto = CreatePostDto.builder()
                .authorId(AUTHOR_ID)
                .content(TEST_CONTENT)
                .published(false)
                .build();

        expectedResponse = ReadPostDto.builder()
                .id(ID)
                .published(true)
                .build();
    }

    @Test
    void createPostSuccessfullyTest() {
        when(postMapper.toEntity(createPostDto)).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(userServiceClient.getFollowerIds(anyLong())).thenReturn(List.of(2L, 3L));
        when(postMapper.toDto(post)).thenReturn(readPostDto);

        ReadPostDto response = postService.create(createPostDto);

        ArgumentCaptor<PostCreatedEvent> eventCaptor = ArgumentCaptor.forClass(PostCreatedEvent.class);
        verify(postEventProducer).sendEvent(eventCaptor.capture());

        PostCreatedEvent sentEvent = eventCaptor.getValue();
        assertEquals(1L, sentEvent.getPostId());
        assertEquals(2L, sentEvent.getAuthorId());
        assertEquals(List.of(2L, 3L), sentEvent.getSubscriberIds());
        assertNotNull(response);
        assertEquals(TEST_CONTENT, response.getContent());
        assertEquals(AUTHOR_ID, response.getAuthorId());
        assertFalse(response.getPublished());
        verify(postValidator).validateDraftPost(createPostDto);
        verify(postRepository).save(any(Post.class));
        verify(postMapper).toDto(post);
    }

    @Test
    void getPostSuccessfullyTest() {
        when(postRepository.findById(ID)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(readPostDto);

        ReadPostDto response = postService.getPost(ID);

        assertNotNull(response);
        assertEquals(ID, response.getId());
        assertEquals(TEST_CONTENT, response.getContent());

        verify(postRepository).findById(ID);
        verify(postMapper).toDto(post);
    }

    @Test
    void updatePostSuccessfullyTest() {
        Post post = new Post();
        post.setContent(TEST_CONTENT);

        ReadPostDto readPostDto = ReadPostDto.builder()
                .content(UPDATE_CONTENT)
                .build();

        UpdatePostDto updatePostDto = new UpdatePostDto(UPDATE_CONTENT, ID);

        when(postRepository.findById(ID)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toDto(any(Post.class))).thenReturn(readPostDto);

        ReadPostDto response = postService.update(ID, updatePostDto);

        assertEquals(UPDATE_CONTENT, response.getContent());
    }

    @Test
    void publishSuccessfullyTest() {
        when(postRepository.findById(ID)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedResponse);

        ReadPostDto response = postService.publish(ID);

        assertNotNull(response);
        assertTrue(response.getPublished());

        verify(postValidator).validateNotPublished(post);
        verify(postValidator).validateNotDeleted(post);
        verify(postValidator).validatePostAuthorExist(post);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    void deletePostSuccessfullyTest() {
        long postId = ID;
        Post post = new Post();
        post.setId(postId);
        post.setDeleted(false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        postService.delete(postId);

        assertTrue(post.isDeleted());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void exceptionWhenPostNotFoundTest() {
        when(postRepository.findById(ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> postService.getPost(ID));

        assertEquals(String.format("Пост с id=%d не найден", ID), exception.getMessage());
        verify(postRepository).findById(ID);
        verifyNoInteractions(postMapper);
    }

    @Test
    void exceptionWhenPostIsDeletedTest() {
        post.setDeleted(true);
        when(postRepository.findById(ID)).thenReturn(Optional.of(post));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> postService.getPost(ID));

        assertEquals(String.format("Пост с id=%d не найден", ID), exception.getMessage());
        verify(postRepository).findById(ID);
        verifyNoInteractions(postMapper);
    }

    @Test
    void exceptionWhenAlreadyPublishedPostTest() {
        when(postRepository.findById(ID)).thenReturn(Optional.of(post));

        doThrow(new DataValidationException(EXCEPTION_MESSAGE))
                .when(postValidator).validateNotPublished(post);

        Exception exception = assertThrows(DataValidationException.class, () -> postService.publish(ID));

        assertEquals(EXCEPTION_MESSAGE, exception.getMessage());

        verify(postValidator).validateNotPublished(post);
        verify(postValidator, never()).validateNotDeleted(post);
        verify(postValidator, never()).validatePostAuthorExist(post);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void getFilteredPosts_ByAuthorId_ReturnsFilteredPosts() {
        PostFilterDto postFilterDto = new PostFilterDto(AUTHOR_ID, null, false);
        List<Post> posts = Collections.singletonList(post);
        List<ReadPostDto> expectedDtos = Collections.singletonList(readPostDto);

        when(postRepository.findByAuthorId(AUTHOR_ID)).thenReturn(posts);
        when(postMapper.toDtoList(posts)).thenReturn(expectedDtos);

        List<ReadPostDto> result = postService.getFilteredPosts(postFilterDto);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(postValidator).validateFilterDto(postFilterDto);
        verify(postRepository).findByAuthorId(AUTHOR_ID);
        verify(postMapper).toDtoList(posts);
    }

    @Test
    void getFilteredPosts_ByProjectId_ReturnsFilteredPosts() {
        PostFilterDto postFilterDto = new PostFilterDto(null, PROJECT_ID, false);
        List<Post> posts = Collections.singletonList(post);
        List<ReadPostDto> expectedDtos = Collections.singletonList(readPostDto);

        when(postRepository.findByProjectId(PROJECT_ID)).thenReturn(posts);
        when(postMapper.toDtoList(posts)).thenReturn(expectedDtos);

        List<ReadPostDto> result = postService.getFilteredPosts(postFilterDto);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(postValidator).validateFilterDto(postFilterDto);
        verify(postRepository).findByProjectId(PROJECT_ID);
        verify(postMapper).toDtoList(posts);
    }

    @Test
    void getFilteredPosts_NoPostsFound_ReturnsEmptyList() {
        PostFilterDto postFilterDto = new PostFilterDto(AUTHOR_ID, null, true);
        List<Post> emptyPosts = Collections.emptyList();
        List<ReadPostDto> emptyDtos = Collections.emptyList();

        when(postRepository.findByAuthorId(AUTHOR_ID)).thenReturn(emptyPosts);
        when(postMapper.toDtoList(emptyPosts)).thenReturn(emptyDtos);

        List<ReadPostDto> result = postService.getFilteredPosts(postFilterDto);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postValidator).validateFilterDto(postFilterDto);
        verify(postRepository).findByAuthorId(AUTHOR_ID);
        verify(postMapper).toDtoList(emptyPosts);
    }

    @Test
    void testCorrectAllUnpublishedPosts() {
        List<Post> posts = List.of(post);
        when(postRepository.findReadyToPublish()).thenReturn(posts);
        when(postRepository.saveAll(posts)).thenReturn(posts);

        postService.correctAllUnpublishedPosts();

        verify(postRepository).findReadyToPublish();
        verify(postCorrector).correctContentPost(post);
        verify(postRepository).saveAll(posts);
    }
}