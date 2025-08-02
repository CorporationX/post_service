package faang.school.postservice.service;

import faang.school.postservice.cache.AuthorCacheService;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaProducerService;
import faang.school.postservice.dto.kafka.PostViewEvent;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.kafka.KafkaPostViewProducer;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.RedisService;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient client;
    @Mock
    private KafkaProducerService kafka;
    @Mock
    private KafkaPostViewProducer kafkaPostViewProducer;

    @Mock
    private AuthorCacheService authorCacheService;

    @Mock
    private RedisService redisService;
    @Spy
    private PostMapperImpl postMapper;
    @Captor
    ArgumentCaptor<Post> postCaptor;
    @InjectMocks
    private PostService postService;

    @Test
    void getPostById() {
        Post post = createPost(1L);
        Long postId = post.getId();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
    }

    @Test
    void getUserByIdTestException() {
        long id = -1L;
        when(postRepository.findById(id))
                .thenThrow(new IllegalArgumentException("There is no such id = " + id));

        assertThrows(IllegalArgumentException.class, () -> postRepository.findById(id));
    }


    @Test
    public void testGetPostById_Success() {
        long postId = 1L;
        long userId = 123L;
        Post post = createPost(postId);
        final PostResponseDto expectedDto = postMapper.toDto(post);

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        doNothing().when(kafkaPostViewProducer).send(any(PostViewEvent.class));

        PostResponseDto result = postService.getPostById(postId, userId);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        ArgumentCaptor<PostViewEvent> eventCaptor = ArgumentCaptor.forClass(PostViewEvent.class);
        verify(kafkaPostViewProducer).send(eventCaptor.capture());

        PostViewEvent sentEvent = eventCaptor.getValue();
        assertEquals(postId, sentEvent.getPostId());
        assertEquals(userId, sentEvent.getUserId());
        assertNotNull(sentEvent.getViewedAt());
    }

    @Test
    public void testGetPostById_PostNotFound() {
        long nonExistentPostId = 999L;
        long userId = 123L;

        when(postRepository.findById(nonExistentPostId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.getPostById(nonExistentPostId, userId));

        assertEquals("There is no such id = " + nonExistentPostId, exception.getMessage());

        verifyNoInteractions(kafkaPostViewProducer);
    }


    @Test
    public void testGetPostResponseDtoByIdNotFound() {
        long postId = 1L;

        when(postRepository.findById(postId))
                .thenThrow(new IllegalArgumentException("There is no such id = " + postId));

        assertThrows(IllegalArgumentException.class, () -> postRepository.findById(postId));
    }

    @Test
    public void testCreateDraftPost() {
        PostRequestDto request = new PostRequestDto("Draft", 1L, null);

        when(postRepository.save(any())).thenReturn(Post.builder().authorId(1L).content("").id(1L).build());
        when(client.getUserFolowees(anyLong())).thenReturn(List.of());

        postService.createDraftPost(request);

        verify(kafka).sendMessage(any(), any());
        verify(postRepository, times(1)).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();
        assertNotNull(savedPost);
    }

    @Test
    public void testPublishPost() {
        Post post = createPost(1L);
        long postId = post.getId();

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        postService.publishPost(postId);

        verify(postRepository).save(postCaptor.capture());
        Post publishedPost = postCaptor.getValue();
        assertTrue(publishedPost.isPublished());
        assertNotNull(publishedPost.getPublishedAt());

        verify(authorCacheService).cacheAuthor(post.getAuthorId());
    }


    @Test
    public void testPublishAlreadyPublishedPost() {
        Post post = createPost(1L);
        long postId = post.getId();
        post.setPublished(true);

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> postService.publishPost(postId));
    }

    @Test
    public void testUpdatePost() {
        Post post = createPost(1L);
        long postId = post.getId();
        post.setContent("Draft");

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        postService.updatePost(postId,
                new PostRequestDto("Post", 1L, null));

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post updatedPost = postCaptor.getValue();

        assertNotNull(updatedPost);
        assertNotEquals("Draft", updatedPost.getContent());
    }

    @Test
    public void testDeletePost() {
        Post post = createPost(1L);
        long postId = post.getId();

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        postService.deletePost(postId);

        assertTrue(post.isDeleted());
    }

    @Test
    public void testGetAllNotDeletedDraftsByAuthorId() {
        Post draft = createPost(1L);
        draft.setAuthorId(2L);
        long draftAuthorId = draft.getAuthorId();

        when(postRepository.findByAuthorId(draftAuthorId))
                .thenReturn(List.of(draft));

        List<PostResponseDto> result = postService.getAllNotDeletedDraftsByAuthorId(draftAuthorId);

        assertEquals(List.of(draft), result.stream()
                .map(postMapper::toEntity)
                .toList());
    }

    @Test
    public void testGetAllNotDeletedDraftsByProjectId() {
        Post draft = createPost(1L);
        draft.setProjectId(2L);
        long draftProjectId = draft.getProjectId();

        when(postRepository.findByProjectId(draftProjectId))
                .thenReturn(List.of(draft));

        List<PostResponseDto> result = postService.getAllNotDeletedDraftsByProjectId(draftProjectId);

        assertEquals(List.of(draft), result.stream()
                .map(postMapper::toEntity)
                .toList());
    }

    @Test
    public void testGetAllPostsByAuthorId() {
        Post post = createPost(1L);
        post.setAuthorId(2L);
        post.setDeleted(false);
        post.setPublished(true);
        long postAuthorId = post.getAuthorId();

        when(postRepository.findByAuthorId(postAuthorId))
                .thenReturn(List.of(post));

        List<PostResponseDto> result = postService.getAllPostsByAuthorId(postAuthorId);

        assertEquals(List.of(post), result.stream()
                .map(postMapper::toEntity)
                .toList());
    }

    @Test
    public void testGetAllPostsByProjectId() {
        Post post = createPost(2L);
        post.setProjectId(2L);
        post.setPublished(true);
        long postProjectId = post.getProjectId();

        when(postRepository.findByProjectId(postProjectId))
                .thenReturn(List.of(post));

        List<PostResponseDto> result = postService.getAllPostsByProjectId(postProjectId);

        assertEquals(List.of(post), result.stream()
                .map(postMapper::toEntity)
                .toList());
    }

    @Test
    void banUsers() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        List<Long> usersId = List.of(userId1, userId2);

        when(postRepository.findAllUsersWhereNotVerifiedMoreN(anyInt())).thenReturn(usersId);

        postService.banUsers();

        verify(redisService, times(2)).sendMessageToBanUsers(anyLong());
    }

    @Test
    void banUsers_EmptyList() {
        List<Long> usersId = List.of();

        when(postRepository.findAllUsersWhereNotVerifiedMoreN(anyInt())).thenReturn(usersId);

        postService.banUsers();

        verify(redisService, times(0)).sendMessageToBanUsers(anyLong());
    }

    private Post createPost(long id) {
        return Post.builder().id(id).build();
    }
}