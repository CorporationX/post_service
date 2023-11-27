package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.PostEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.publisher.KafkaPostProducer;
import faang.school.postservice.publisher.KafkaPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PublisherService publisherService;
    @Mock
    private RedisCacheService redisCacheService;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapper postMapper = new PostMapperImpl();
    @Mock
    private ModerationDictionary moderationDictionary;
    @Mock
    private KafkaPostProducer kafkaPostPublishEventPublisher;
    @Mock
    private KafkaPostViewProducer kafkaPostViewEventPublisher;
    @Mock
    private Executor threadPoolForPostModeration;
    @Mock
    private ThreadPoolTaskExecutor postEventTaskExecutor;
    @Mock
    private PostValidator postValidator;
    @InjectMocks
    private PostService postService;

    private PostDto publishPostDto;
    private PostDto updatePostDto;

    private Post post;
    private Post secondPost;

    private List<Post> twoPostsList;

    private RedisPost redisPost;

    private UserDto userDto;

    private LocalDateTime currentTime;
    private LocalDateTime scheduledAt;
    private LocalDateTime updatedScheduledAt;

    private final Long authorId = 1L;
    private final Long postId = 1L;
    private final Long secondPostId = 2L;
    private final Long firstFollowerId = 10L;
    private final Long secondFollowerId = 20L;
    private final String content = "Content";
    private final String updatedContent = "UpdatedContent";

    @BeforeEach
    void initData() {
        ReflectionTestUtils.setField(postService, "sublistSize", 100);
        ReflectionTestUtils.setField(postService, "batchSize", 1000);
        currentTime = LocalDateTime.now();
        scheduledAt = LocalDateTime.now().plusDays(1);
        updatedScheduledAt = LocalDateTime.now().plusDays(2);
        publishPostDto = PostDto.builder()
                .content(content)
                .authorId(authorId)
                .build();
        updatePostDto = PostDto.builder()
                .id(postId)
                .content(updatedContent)
                .scheduledAt(updatedScheduledAt)
                .build();
        post = Post.builder()
                .id(postId)
                .content(content)
                .authorId(authorId)
                .published(false)
                .deleted(false)
                .scheduledAt(scheduledAt)
                .createdAt(currentTime)
                .build();
        secondPost = Post.builder()
                .id(secondPostId)
                .content(updatedContent)
                .authorId(authorId)
                .published(true)
                .deleted(true)
                .scheduledAt(updatedScheduledAt)
                .createdAt(currentTime)
                .build();
        twoPostsList = new ArrayList<>(List.of(post, secondPost));
        redisPost = RedisPost.builder()
                .postId(postId)
                .content(content)
                .authorId(authorId)
                .version(1)
                .build();
        userDto = UserDto.builder()
                .id(authorId)
                .followerIds(List.of(firstFollowerId, secondFollowerId))
                .build();
    }

    @Test
    void crateDraftPostTest() {
        when(postRepository.save(postMapper.toEntity(publishPostDto))).thenReturn(post);

        PostDto result = postService.crateDraftPost(publishPostDto);

        assertEquals(postMapper.toDto(post), result);

        verify(postValidator).validateData(publishPostDto);
        verify(postRepository).save(postMapper.toEntity(publishPostDto));
    }

    @Test
    void publishPostTest() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(redisCacheService.findUserBy(authorId)).thenReturn(userDto);
        when(redisCacheService.mapPostToRedisPostAndSetDefaultVersion(any(Post.class))).thenReturn(redisPost);

        PostDto result = postService.publishPost(postId);

        assertTrue(result.isPublished());

        verify(postRepository).findById(postId);
        verify(publisherService).publishPostEventToRedis(any(Post.class));
        verify(redisCacheService).findUserBy(authorId);
        verify(redisCacheService).updateOrCacheUser(userDto);
        verify(redisCacheService).mapPostToRedisPostAndSetDefaultVersion(any(Post.class));
        verify(redisCacheService).saveRedisPost(redisPost);
        verify(postEventTaskExecutor).execute(any(Runnable.class));
    }

    @Test
    void updatePostTestFirstScenario() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostDto expected = postMapper.toDto(post);
        expected.setContent(updatedContent);

        PostDto result = postService.updatePost(updatePostDto);

        assertEquals(updatedContent, result.getContent());

        verify(postRepository).findById(postId);
        verify(postValidator).validateAuthorUpdate(post, updatePostDto);
    }

    @Test
    void updatePostTestSecondScenario() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(redisCacheService.findUserBy(authorId)).thenReturn(userDto);

        post.setDeleted(true);

        PostDto result = postService.updatePost(updatePostDto);

        assertEquals(updatedScheduledAt, result.getScheduledAt());

        verify(redisCacheService).findUserBy(authorId);
        verify(redisCacheService).updateOrCacheUser(userDto);
        verify(postEventTaskExecutor).execute(any(Runnable.class));
    }

    @Test
    void updatePostTestThirdScenario() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(redisCacheService.findUserBy(authorId)).thenReturn(userDto);

        post.setDeleted(true);
        userDto.setFollowerIds(Collections.emptyList());

        postService.updatePost(updatePostDto);

        verify(redisCacheService).findUserBy(authorId);
        verify(redisCacheService).updateOrCacheUser(userDto);
        verify(kafkaPostPublishEventPublisher).publish(any(PostEvent.class));
    }

    @Test
    void updatePostTestFourthScenario() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(redisCacheService.findUserBy(authorId)).thenReturn(userDto);

        post.setPublished(true);

        postService.updatePost(updatePostDto);

        verify(redisCacheService).findUserBy(authorId);
        verify(redisCacheService).updateOrCacheUser(userDto);
        verify(kafkaPostPublishEventPublisher).publish(any(PostEvent.class));
    }

    @Test
    void softDeleteFirstScenarioTest() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostDto result = postService.softDelete(postId);

        assertTrue(result.isDeleted());
        verify(postRepository).findById(postId);
    }

    @Test
    void softDeleteSecondScenarioTest() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(redisCacheService.findUserBy(authorId)).thenReturn(userDto);

        post.setPublished(true);

        PostDto result = postService.softDelete(postId);

        assertTrue(result.isDeleted());

        verify(redisCacheService).findUserBy(authorId);
        verify(redisCacheService).updateOrCacheUser(userDto);
        verify(postEventTaskExecutor).execute(any(Runnable.class));
    }

    @Test
    void getPostTest() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        post.setPublished(true);

        PostDto result = postService.getPost(postId);

        assertEquals(postMapper.toDto(post), result);

        verify(postRepository).findById(postId);
        verify(publisherService).publishPostEventToRedis(post);
        verify(postEventTaskExecutor).execute(any(Runnable.class));
    }

    @Test
    void getUserDraftsTest() {
        when(postRepository.findByAuthorId(authorId)).thenReturn(twoPostsList);

        List<PostDto> result = postService.getUserDrafts(authorId);

        assertEquals(1, result.size());
        assertEquals(postMapper.toDto(post), result.get(0));

        verify(postRepository).findByAuthorId(authorId);
        verify(postValidator).validateUserId(authorId);
    }

    @Test
    void getProjectDraftsTest() {
        when(postRepository.findByProjectId(1L)).thenReturn(twoPostsList);

        secondPost.setPublished(false);
        secondPost.setDeleted(false);

        PostDto firstExpected = postMapper.toDto(post);
        PostDto secondExpected = postMapper.toDto(secondPost);

        List<PostDto> result = postService.getProjectDrafts(1L);

        assertEquals(2, result.size());
        assertEquals(secondExpected, result.get(1));
        assertEquals(List.of(firstExpected, secondExpected), result);

        verify(postRepository).findByProjectId(1L);
        verify(postValidator).validateProjectId(1L);
    }

    @Test
    void getUserPostsTest() {
        when(postRepository.findByAuthorIdWithLikes(authorId)).thenReturn(twoPostsList);

        post.setPublished(true);

        List<PostDto> result = postService.getUserPosts(authorId);

        assertEquals(1, result.size());
        assertEquals(postMapper.toDto(post), result.get(0));

        verify(postValidator).validateUserId(authorId);
        verify(postRepository).findByAuthorIdWithLikes(authorId);
        verify(publisherService).publishPostEventToRedis(post);
        verify(postEventTaskExecutor).execute(any(Runnable.class));
    }

    @Test
    void getProjectPostsTest() {
        when(postRepository.findByProjectIdWithLikes(1L)).thenReturn(twoPostsList);

        post.setPublished(true);
        secondPost.setDeleted(false);

        PostDto firstExpected = postMapper.toDto(post);
        PostDto secondExpected = postMapper.toDto(secondPost);

        List<PostDto> result = postService.getProjectPosts(1L);

        assertEquals(2, result.size());
        assertEquals(secondExpected, result.get(1));
        assertEquals(List.of(firstExpected, secondExpected), result);

        verify(postValidator).validateProjectId(1L);
        verify(postRepository).findByProjectIdWithLikes(1L);
        verify(publisherService, times(2)).publishPostEventToRedis(any(Post.class));
    }

    @Test
    void findByPostTest() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Post result = postService.findPostBy(postId);
        assertEquals(post, result);
    }

    @Test
    void findAlreadyPublishedAndNotDeletedPostTest() {
        when(postRepository.findPublishedAndNotDeletedBy(postId)).thenReturn(Optional.empty());
        Optional<Post> result = postService.findAlreadyPublishedAndNotDeletedPost(postId);
        assertTrue(result.isEmpty());
    }

    @Test
    void findSortedPostsByAuthorIdsLimitTest() {
        when(postRepository.findSortedPostsByAuthorIdsAndLimit(List.of(authorId), 2)).thenReturn(List.of(post, secondPost));
        List<Post> result = postService.findSortedPostsByAuthorIdsLimit(List.of(authorId), 2);
        assertEquals(2, result.size());
    }

    @Test
    void findSortedPostByAuthorIdsNotInPostIdsLimit() {
        when(postRepository.findSortedPostsByAuthorIdsNotInPostIdsLimit(List.of(authorId), List.of(postId), 2))
                .thenReturn(List.of(secondPost));
        List<Post> result = postService.findSortedPostsByAuthorIdsNotInPostIdsLimit(List.of(authorId), List.of(postId), 2);
        assertEquals(1, result.size());
        assertEquals(List.of(secondPost), result);
    }

    @Test
    void findRedisPostsByAndCacheThenIfNotExist() {
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.empty());
        when(postRepository.findPublishedAndNotDeletedBy(postId)).thenReturn(Optional.of(post));
        when(redisCacheService.cachePost(post)).thenReturn(redisPost);

        List<RedisPost> result = postService.findRedisPostsByAndCacheThemIfNotExist(List.of(postId));

        assertEquals(redisPost, result.get(0));

        verify(redisCacheService).findRedisPostBy(postId);
        verify(postRepository).findPublishedAndNotDeletedBy(postId);
        verify(redisCacheService).cachePost(post);
    }
}