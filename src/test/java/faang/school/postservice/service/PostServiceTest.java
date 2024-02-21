package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.CreatePostEvent;
import faang.school.postservice.dto.kafka.PostViewEvent;
import faang.school.postservice.exception.AlreadyDeletedException;
import faang.school.postservice.exception.AlreadyPostedException;
import faang.school.postservice.exception.NoPublishedPostException;
import faang.school.postservice.exception.SamePostAuthorException;
import faang.school.postservice.exception.UpdatePostException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.mapper.redis.*;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.publisher.KafkaPostProducer;
import faang.school.postservice.publisher.KafkaPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import faang.school.postservice.validator.PostValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private RedisUserRepository redisUserRepository;
    @Mock
    private RedisFeedRepository redisFeedRepository;
    @Spy
    private PostMapperImpl postMapper;
    @Spy
    private RedisCommentMapper redisCommentMapper = new RedisCommentMapperImpl();
    @Spy
    private RedisPostMapper redisPostMapper = new RedisPostMapperImpl(redisCommentMapper);
    @Spy
    private RedisUserMapperImpl redisUserMapper;
    @Mock
    private ModerationDictionary moderationDictionary;
    @Mock
    private Executor threadPoolForPostModeration;
    @Mock
    private PublisherService publisherService;
    @Mock
    private KafkaPostProducer kafkaPostProducer;
    @Mock
    private KafkaPostViewProducer kafkaPostViewProducer;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectService;
    @Mock
    private PostValidator postValidator;
    @InjectMocks
    private PostService postService;

    private PostDto incorrectPostDto;
    private PostDto correctPostDto;
    private Post alreadyPublishedPost;
    private Post correctPost;
    private Post post1;
    private Post post2;
    private Post post3;
    private final Long CORRECT_ID = 1L;
    private final long INCORRECT_ID = 0;

    @BeforeEach
    void initData() {
        incorrectPostDto = PostDto.builder()
                .id(INCORRECT_ID)
                .content("content")
                .projectId(CORRECT_ID)
                .authorId(CORRECT_ID)
                .build();
        correctPostDto = PostDto.builder()
                .id(CORRECT_ID)
                .content("content")
                .authorId(CORRECT_ID)
                .build();
        alreadyPublishedPost = Post.builder()
                .id(2L)
                .published(true)
                .build();
        correctPost = Post.builder()
                .content("content")
                .id(CORRECT_ID)
                .authorId(CORRECT_ID)
                .build();
    }

    @Test
    void testCreateDaftPostWithSameAuthor() {
        assertThrows(SamePostAuthorException.class, () -> postService.crateDraftPost(incorrectPostDto));
    }

    @Test
    void testCreateDaftPostWithNonExistentUser() {
        incorrectPostDto.setProjectId(null);
        when(userServiceClient.getUser(CORRECT_ID)).thenThrow(FeignException.class);

        assertThrows(EntityNotFoundException.class, () -> postService.crateDraftPost(incorrectPostDto));
    }

    @Test
    void testCreateDaftPostWithNonExistentProject() {
        incorrectPostDto.setAuthorId(null);
        when(projectService.getProject(CORRECT_ID)).thenThrow(FeignException.class);

        assertThrows(EntityNotFoundException.class, () -> postService.crateDraftPost(incorrectPostDto));
    }

    @Test
    void testCreateDaftPost() {
        Post post = postMapper.toEntity(correctPostDto);
        when(postRepository.save(post)).thenReturn(post);

        PostDto actualPostDto = postService.crateDraftPost(correctPostDto);
        assertEquals(correctPostDto, actualPostDto);
    }

    @Test
    void testPublishPostWithoutPostInDB() {
        when(postRepository.findById(CORRECT_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> postService.publishPost(CORRECT_ID));
    }

    @Test
    void testPublishPostWithAlreadyPublishedPost() {
        when(postRepository.findById(CORRECT_ID)).thenReturn(Optional.ofNullable(alreadyPublishedPost));
        assertThrows(AlreadyPostedException.class, () -> postService.publishPost(CORRECT_ID));
    }

    @Test
    void testPublishedPostWithDeletedPost() {
        correctPost.setDeleted(true);
        returnCorrectPostForPostRepository();
        assertThrows(AlreadyDeletedException.class, () -> postService.publishPost(CORRECT_ID));
    }

    @Test
    void testPublishPost() {
        RedisUser redisUser = new RedisUser();
        redisUser.setId(2L);
        redisUser.setFollowerIds(List.of(2L));
        redisUser.setVersion(1L);

        RedisFeed redisFeed = new RedisFeed();
        redisFeed.setUserId(2L);
        redisFeed.setPostIds(new LinkedHashSet<>(Collections.singletonList(1L)));

        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setPublished(false);
        post.setDeleted(false);
        //post.setScheduledAt(LocalDateTime.now().plusMinutes(30));

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setFollowers(List.of(2L));

        //Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        Mockito.when(postValidator.validatePostId(post.getId())).thenReturn(post);
        Mockito.when(userServiceClient.getUser(post.getAuthorId())).thenReturn(userDto);
        Mockito.when(postRepository.findByAuthorId(userDto.getId())).thenReturn(Collections.singletonList(post));

        PostDto result = postService.publishPost(post.getId());

        assertNotNull(result);
        assertTrue(result.isPublished());
        assertNotNull(result.getPublishedAt());

        verify(publisherService).publishPostEventToRedis(post);
        verify(redisPostRepository).save(Mockito.any(RedisPost.class));
        verify(userServiceClient).getUser(userDto.getId());
        verify(redisUserRepository).save(redisUser);
        verify(redisFeedRepository).save(redisFeed);
        verify(kafkaPostProducer).publishPostEvent(Mockito.any(CreatePostEvent.class));
    }

    @Test
    void testUpdatePostWithoutPostInDB() {
        when(postRepository.findById(INCORRECT_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> postService.updatePost(incorrectPostDto));
    }

    @Test
    void testUpdatePostWithIncorrectAuthor() {
        correctPostDto.setAuthorId(2L);
        returnCorrectPostForPostRepository();

        assertThrows(UpdatePostException.class, () -> postService.updatePost(correctPostDto));
    }

    @Test
    void testUpdatePost() {
        correctPostDto.setContent("other content");
        correctPostDto.setScheduledAt(LocalDateTime.now().plusMonths(1));
        correctPost.setScheduledAt(LocalDateTime.now());
        returnCorrectPostForPostRepository();

        PostDto actualPostDto = postService.updatePost(correctPostDto);
        assertEquals(correctPostDto, actualPostDto);
    }

    @Test
    void testSoftDeleteWithoutPostInDB() {
        when(postRepository.findById(CORRECT_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> postService.softDelete(CORRECT_ID));
    }

    @Test
    void testSoftDeleteWithAlreadyDeletedPost() {
        correctPost.setDeleted(true);
        returnCorrectPostForPostRepository();

        assertThrows(AlreadyDeletedException.class, () -> postService.softDelete(CORRECT_ID));
    }

    @Test
    void testSoftDelete() {
        correctPostDto.setDeleted(true);
        returnCorrectPostForPostRepository();

        PostDto actualPostDto = postService.softDelete(CORRECT_ID);
        assertEquals(correctPostDto, actualPostDto);
    }

    @Test
    void testGetPostWithoutPostInDB() {
        when(postRepository.findById(CORRECT_ID)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> postService.getPost(CORRECT_ID));
    }

    @Test
    void testGetPostAlreadyDeleted() {
        correctPost.setDeleted(true);
        returnCorrectPostForPostRepository();

        assertThrows(AlreadyDeletedException.class, () -> postService.getPost(CORRECT_ID));
    }

    @Test
    void testGetPostWhichNoPublished() {
        returnCorrectPostForPostRepository();
        assertThrows(NoPublishedPostException.class, () -> postService.getPost(CORRECT_ID));
    }

    @Test
    void testGetPost() {
        long postId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        Post post = new Post();
        post.setId(postId);
        post.setPublished(true);
        post.setDeleted(false);
        post.setPublishedAt(LocalDateTime.now().minusMinutes(30));
        post.setAuthorId(userDto.getId());

        RedisPost redisPost = new RedisPost();
        redisPost.setPostViews(10L);

        PostDto postDto = new PostDto();
        postDto.setId(postId);
        postDto.setPublished(true);
        postDto.setPublishedAt(post.getPublishedAt());

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Mockito.when(redisPostRepository.findById(postId)).thenReturn(Optional.of(redisPost));
        Mockito.when(postMapper.toDto(post)).thenReturn(postDto);
        Mockito.doNothing().when(publisherService).publishPostEventToRedis(post);
        Mockito.doNothing().when(kafkaPostViewProducer).publishPostViewEvent(Mockito.any(PostViewEvent.class));

        PostService postService = new PostService(postRepository, redisPostRepository, redisUserRepository, redisFeedRepository, postValidator,
                postMapper, redisPostMapper, redisUserMapper, moderationDictionary, threadPoolForPostModeration, publisherService, kafkaPostProducer, kafkaPostViewProducer, userServiceClient);

        PostDto result = postService.getPost(postId);

        assertNotNull(result);
        assertTrue(result.isPublished());
        assertNotNull(result.getPublishedAt());
    }

    @Test
    void testDoPostModeration() {
        when(postRepository.findNotVerified()).thenReturn(getNotVerifiedPosts());
        ReflectionTestUtils.setField(postService, "sublistSize", 100);
        postService.doPostModeration();

        verify(threadPoolForPostModeration).execute(any());
    }

    @Test
    void testDoPostModerationWithAsync() {
        when(postRepository.findNotVerified()).thenReturn(getNotVerifiedPosts());
        ReflectionTestUtils.setField(postService, "sublistSize", 2);
        postService.doPostModeration();

        verify(threadPoolForPostModeration, times(2)).execute(any());
    }

    private void returnCorrectPostForPostRepository() {
        when(postRepository.findById(CORRECT_ID)).thenReturn(Optional.ofNullable(correctPost));
    }

    private List<Post> getNotVerifiedPosts() {
        post1 = Post.builder()
                .content("some content")
                .build();
        post2 = Post.builder()
                .content("post number two")
                .build();
        post3 = Post.builder()
                .content("This is the best bootcamp!")
                .build();
        return List.of(post1, post2, post3);
    }
}