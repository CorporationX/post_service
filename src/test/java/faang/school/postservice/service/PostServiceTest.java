package faang.school.postservice.service;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.RedisPostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.user.UserCacheDto;
import faang.school.postservice.event.PostEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserCacheRepository;
import faang.school.postservice.utils.PostSpecifications;
import faang.school.postservice.validator.HashtagValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper;

    @Mock
    private PostValidator postValidator;

    @Mock
    private HashtagValidator hashtagValidator;

    @Mock
    private HashtagService hashtagService;

    @Mock
    private PostCacheRepository postCacheRepository;

    @Mock
    private ExecutorService executorService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private KafkaPostProducer kafkaPostProducer;

    @Mock
    private UserFeignService userFeignService;

    @Mock
    UserCacheRepository userCacheRepository;

    @InjectMocks
    private PostService postService;
    private CountDownLatch latch;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postService, "userBansChannelName", "user_ban_channel");
        ReflectionTestUtils.setField(postService, "batchSize", 100);
    }

    long userId = 1L;

    Post firstPost = new Post();
    Post secondPost = new Post();

    Post post = createTestPost();

    RedisPostDto redisPostDto = createTestRedisPostDto();
    ResponsePostDto firstResponsePostDto = new ResponsePostDto();
    ResponsePostDto secondResponsePostDto = new ResponsePostDto();

    @Test
    void createPostShouldReturnResponsePostDto() {
        CreatePostDto createPostDto = new CreatePostDto();
        createPostDto.setContent("Test content");
        createPostDto.setAuthorId(1L);
        createPostDto.setProjectId(1L);
        createPostDto.setHashtags(Collections.singletonList("test"));

        Post post = createTestPost();

        ResponsePostDto responsePostDto = new ResponsePostDto();
        responsePostDto.setId(1L);
        responsePostDto.setContent("Test content");

        when(postMapper.toEntity(createPostDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(responsePostDto);
        when(postMapper.toRedisPostDto(post)).thenReturn(redisPostDto);

        UserCacheDto userCacheDto = createUserCacheDto();
        when(userCacheRepository.getUserFromCache(anyString())).thenReturn(null);
        when(userFeignService.getCacheUser(1L)).thenReturn(userCacheDto);


        ResponsePostDto result = postService.create(createPostDto);


        assertEquals(responsePostDto, result);
        verify(postValidator, times(1)).validateContent("Test content");
        verify(postValidator, times(1)).validateAuthorIdAndProjectId(1L, 1L);
        verify(postValidator, times(1)).validateAuthorId(1L);
        verify(postValidator, times(1)).validateProjectId(1L, 1L);
        verify(postRepository, times(1)).save(post);
        verify(postCacheRepository, times(1)).savePostToCache(any(RedisPostDto.class));
        verify(kafkaPostProducer, times(1)).sendPostEvent(any(PostEvent.class));

        ArgumentCaptor<PostEvent> postEventCaptor = ArgumentCaptor.forClass(PostEvent.class);
        verify(kafkaPostProducer, times(1)).sendPostEvent(postEventCaptor.capture());

        PostEvent capturedPostEvent = postEventCaptor.getValue();
        assertEquals(1L, capturedPostEvent.postId());
        assertEquals(1L, capturedPostEvent.authorId());
        assertEquals(List.of(1L, 2L), capturedPostEvent.followerIds());
    }

    @Test
    void createShouldThrowExceptionWhenContentIsInvalid() {
        CreatePostDto createPostDto = new CreatePostDto();
        createPostDto.setContent("");
        createPostDto.setAuthorId(1L);
        createPostDto.setProjectId(2L);

        doThrow(new DataValidationException("Content cannot be blank")).when(postValidator).validateContent(createPostDto.getContent());

        assertThrows(DataValidationException.class, () -> postService.create(createPostDto));

        verify(postValidator, times(1)).validateContent(createPostDto.getContent());
    }

    @Test
    void publishShouldSetPublishedAndReturnPostDto() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(false);

        ResponsePostDto expectedDto = new ResponsePostDto();
        expectedDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedDto);

        ResponsePostDto result = postService.publish(postId);

        verify(postValidator, times(1)).validateExistingPostId(postId);
        verify(postValidator, times(1)).validatePostIdOnPublished(postId);

        assertEquals(true, post.isPublished());
        assertEquals(expectedDto, result);

        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    void updateShouldUpdateContentAndReturnPostDto() {
        Long postId = 1L;
        String newContent = "Updated content";
        UpdatePostDto updatePostDto = new UpdatePostDto();
        updatePostDto.setContent(newContent);

        Post post = new Post();
        post.setId(postId);
        post.setContent("Old content");

        ResponsePostDto expectedDto = new ResponsePostDto();
        expectedDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedDto);

        ResponsePostDto result = postService.update(postId, updatePostDto);

        verify(postValidator, times(1)).validateExistingPostId(postId);
        verify(postValidator, times(1)).validateContent(newContent);

        assertEquals(newContent, post.getContent());
        assertEquals(expectedDto, result);

        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    void shouldUpdatePostWithNewAndExistingHashtags() {
        Long postId = 1L;
        UpdatePostDto updatePostDto = new UpdatePostDto();
        updatePostDto.setContent("Updated content");
        updatePostDto.setHashtags(List.of("tag1", "tag3"));

        Hashtag firstTag = Hashtag.builder().tag("tag1").build();
        Hashtag secondTag = Hashtag.builder().tag("tag2").build();
        Hashtag thirdTag = Hashtag.builder().tag("tag3").build();

        Post existingPost = new Post();
        existingPost.setContent("Old content");
        existingPost.setHashtags(new HashSet<>(Set.of(firstTag, secondTag)));
        existingPost.setCreatedAt(LocalDateTime.now());
        existingPost.setUpdatedAt(null);

        Post updatedPost = new Post();
        updatedPost.setContent("Updated content");
        updatedPost.setHashtags(new HashSet<>(Set.of(firstTag, thirdTag)));
        updatedPost.setCreatedAt(existingPost.getCreatedAt());
        updatedPost.setUpdatedAt(LocalDateTime.now());

        ResponsePostDto responseDto = new ResponsePostDto();

        doNothing().when(postValidator).validateExistingPostId(postId);
        doNothing().when(postValidator).validateContent(updatePostDto.getContent());
        when(postMapper.toDto(any(Post.class))).thenReturn(responseDto);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(updatedPost);

        doNothing().when(hashtagValidator).validateHashtag(anyString());

        when(hashtagService.findAllByTags(List.of("tag1", "tag3")))
                .thenReturn(List.of(firstTag));
        when(hashtagService.create("tag3")).thenReturn(thirdTag);

        ResponsePostDto result = postService.update(postId, updatePostDto);

        verify(postValidator, times(1)).validateExistingPostId(postId);
        verify(postValidator, times(1)).validateContent(updatePostDto.getContent());
        verify(hashtagValidator, times(1)).validateHashtag("tag1");
        verify(hashtagValidator, times(1)).validateHashtag("tag3");

        assertEquals("Updated content", existingPost.getContent());
        assertTrue(existingPost.getHashtags().contains(firstTag));
        assertNotNull(existingPost.getUpdatedAt());
        verify(postRepository, times(1)).save(existingPost);
        assertEquals(responseDto, result);
    }

    @Test
    void deleteShouldMarkPostAsDeletedAndSave() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setDeleted(false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.delete(postId);

        verify(postValidator, times(1)).validateExistingPostId(postId);
        verify(postValidator, times(1)).validatePostIdOnRemoved(postId);

        assert (post.isDeleted());

        verify(postRepository, times(1)).save(post);
    }

    @Test
    void getByIdShouldValidateAndReturnPostDto() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        ResponsePostDto responsePostDto = new ResponsePostDto();
        responsePostDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(responsePostDto);

        ResponsePostDto result = postService.getById(postId);

        verify(postValidator, times(1)).validateExistingPostId(postId);
        verify(postValidator, times(1)).validatePostIdOnRemoved(postId);
        verify(postRepository, times(1)).findById(postId);
        verify(postMapper, times(1)).toDto(post);

        assertEquals(responsePostDto, result);
    }

    @Test
    void getDraftByUserIdShouldValidateAndReturnDraftsPosts() {
        when(postRepository.findReadyToPublishByAuthor(userId)).thenReturn(List.of(firstPost, secondPost));
        when(postMapper.toDto(firstPost)).thenReturn(firstResponsePostDto);
        when(postMapper.toDto(secondPost)).thenReturn(secondResponsePostDto);

        List<ResponsePostDto> result = postService.getDraftsByUserId(userId);

        verify(postValidator, times(1)).validateAuthorId(userId);
        verify(postRepository, times(1)).findReadyToPublishByAuthor(userId);

        assertEquals(List.of(firstResponsePostDto, secondResponsePostDto), result);
    }

    @Test
    void getDraftByProjectIdShouldValidateAndReturnDraftsPosts() {
        when(postRepository.findReadyToPublishByProject(userId)).thenReturn(List.of(firstPost, secondPost));
        when(postMapper.toDto(firstPost)).thenReturn(firstResponsePostDto);
        when(postMapper.toDto(secondPost)).thenReturn(secondResponsePostDto);

        List<ResponsePostDto> result = postService.getDraftsByProjectId(userId);

        verify(postValidator, times(1)).validateAuthorId(userId);
        verify(postRepository, times(1)).findReadyToPublishByProject(userId);

        assertEquals(List.of(firstResponsePostDto, secondResponsePostDto), result);
    }

    @Test
    void getPublishedByUserIdShouldValidateAndReturnPublishedPosts() {
        when(postRepository.findPublishedByAuthor(userId)).thenReturn(List.of(firstPost, secondPost));
        when(postMapper.toDto(firstPost)).thenReturn(firstResponsePostDto);
        when(postMapper.toDto(secondPost)).thenReturn(secondResponsePostDto);

        List<ResponsePostDto> result = postService.getPublishedByUserId(userId);

        verify(postValidator, times(1)).validateAuthorId(userId);
        verify(postRepository, times(1)).findPublishedByAuthor(userId);

        assertEquals(List.of(firstResponsePostDto, secondResponsePostDto), result);
    }

    @Test
    void getPublishedByProjectIdShouldValidateAndReturnPublishedPosts() {
        long projectId = 1L;
        long authorId = 1L;

        when(postRepository.findPublishedByProject(projectId)).thenReturn(List.of(firstPost, secondPost));
        when(postMapper.toDto(firstPost)).thenReturn(firstResponsePostDto);
        when(postMapper.toDto(secondPost)).thenReturn(secondResponsePostDto);

        List<ResponsePostDto> result = postService.getPublishedByProjectId(projectId, authorId);

        verify(postValidator, times(1)).validateProjectId(projectId, authorId);
        verify(postRepository, times(1)).findPublishedByProject(projectId);

        assertEquals(List.of(firstResponsePostDto, secondResponsePostDto), result);
    }

    @Test
    void shouldReturnPostsWhenHashtagExists() {
        String existingTag = "#existing";

        Post post = new Post();
        ResponsePostDto responsePostDto = new ResponsePostDto();

        when(postRepository.findByHashtags(existingTag)).thenReturn(List.of(post));
        when(postMapper.toDto(post)).thenReturn(responsePostDto);

        List<ResponsePostDto> result = postService.findByHashtags(existingTag);

        assertEquals(List.of(responsePostDto), result);

        verify(hashtagValidator, times(1)).validateHashtag(existingTag);
        verify(postRepository, times(1)).findByHashtags(existingTag);
        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    void shouldReturnEmptyListWhenNoPostsFound() {
        String existingTag = "#existing";

        when(postRepository.findByHashtags(existingTag)).thenReturn(Collections.emptyList());

        List<ResponsePostDto> result = postService.findByHashtags(existingTag);

        assertEquals(Collections.emptyList(), result);

        verify(hashtagValidator, times(1)).validateHashtag(existingTag);
        verify(postRepository, times(1)).findByHashtags(existingTag);
        verify(postMapper, never()).toDto(any(Post.class));
    }

    @DisplayName("Get post with valid id")
    @Test
    void testGetPostByIdValidId() {
        lenient().when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Post result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals(post, result);
    }

    @Test
    @DisplayName("Get post with invalid id")
    void testGetPostByIdInvalidId() {
        lenient().when(postRepository.findById(1L)).thenReturn(Optional.empty());


        Exception ex = assertThrows(EntityNotFoundException.class, () -> postService.getPostById(1L));
        assertEquals("Post with id: 1 not found", ex.getMessage());
    }

    private Post createTestPost() {
        return Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Test content")
                .build();
    }

    @Test
    void publishScheduledPosts() {
        Post post1 = new Post();
        post1.setPublished(false);
        Post post2 = new Post();
        post2.setPublished(false);
        List<Post> postsToPublish = Arrays.asList(post1, post2);

        when(postRepository.findAll(PostSpecifications.isReadyToPublish())).thenReturn(postsToPublish);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(executorService).execute(any(Runnable.class));

        postService.publishScheduledPosts();

        verify(postRepository, times(1)).findAll(PostSpecifications.isReadyToPublish());
        verify(postRepository, times(1)).saveAll(any());

        assertTrue(post1.isPublished(), "Post 1 should be published");
        assertNotNull(post1.getPublishedAt(), "Post 1 publishedAt should not be null");
        assertTrue(post2.isPublished(), "Post 2 should be published");
        assertNotNull(post2.getPublishedAt(), "Post 2 publishedAt should not be null");
    }

    private RedisPostDto createTestRedisPostDto() {
        RedisPostDto redisPost = new RedisPostDto(
                1L,
                "Test content",
                101L,
                202L,
                10,
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                Set.of(1L, 2L)
        );
        return redisPost;
    }

    private UserCacheDto createUserCacheDto() {
        return new UserCacheDto(1L, "John", List.of(1L, 2L));
    }
}