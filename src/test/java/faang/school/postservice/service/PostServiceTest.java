package faang.school.postservice.service;

import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.cache.service.PostRedisService;
import faang.school.postservice.cache.service.UserRedisService;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.exception.post.PostAlreadyDeletedException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostWithTwoAuthorsException;
import faang.school.postservice.exception.post.PostWithoutAuthorException;
import faang.school.postservice.kafka.event.post.PostPublishedEvent;
import faang.school.postservice.kafka.event.post.PostViewedEvent;
import faang.school.postservice.kafka.producer.KafkaProducer;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.container.PostContainer;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Autowired
    private PostService postService;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private PostRedisService postRedisService;
    @MockBean
    private UserRedisService userRedisService;
    @MockBean
    private UserServiceClient userServiceClient;
    @MockBean
    private PostValidator validator;
    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private PostMapperImpl mapper;
    @Captor
    private ArgumentCaptor<Post> postCaptor;
    private PostContainer container;
    private Post entity;
    private PostDto dto;
    @Value("${spring.kafka.topic.post.published}")
    private String postPublishedTopic;
    @Value("${spring.kafka.topic.post.viewed}")
    private String postViewedTopic;

    @BeforeEach
    void setUp() {
        container = new PostContainer();
        entity = container.entity();
        dto = container.dto();
    }

    @Test
    void testCreateWithInvalidAuthor() {
        // given
        PostDto dtoWOAuthors = PostDto.builder()
                .id(container.postId())
                .build();

        PostDto dtoWithTwoAuthors = PostDto.builder()
                .id(container.postId() + 1)
                .build();

        // when
        doThrow(PostWithoutAuthorException.class).when(validator).validateBeforeCreate(dtoWOAuthors);
        doThrow(PostWithTwoAuthorsException.class).when(validator).validateBeforeCreate(dtoWithTwoAuthors);

        // then
        assertThrows(PostWithoutAuthorException.class, () -> postService.create(dtoWOAuthors));
        assertThrows(PostWithTwoAuthorsException.class, () -> postService.create(dtoWithTwoAuthors));
    }

    @Test
    void testCreateSuccessfully() {
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(postRepository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        PostDto actual = postService.create(dto);

        verify(validator, times(1)).validateBeforeCreate(dto);
        verify(postRepository, times(1)).save(entity);
        assertEquals(dto, actual);
    }

    @Test
    void testPublishTwiceWhenPublishing() {
        // given
        Long postId = container.postId();
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
        doThrow(PostAlreadyPublishedException.class).when(validator).validateBeforePublishing(entity);

        // then
        assertThrows(PostAlreadyPublishedException.class, () -> postService.publish(postId));
        verify(postRepository, times(0)).save(any());
    }

    @Test
    void testPostDeletedWhenPublishing() {
        Long postId = container.postId();
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
        doThrow(PostAlreadyDeletedException.class).when(validator).validateBeforePublishing(entity);

        assertThrows(PostAlreadyDeletedException.class, () -> postService.publish(postId));
        verify(postRepository, times(0)).save(any());
    }

    @Test
    void testPublish() {
        // given
        Long postId = entity.getId();
        LocalDateTime currentTime = LocalDateTime.now();
        UserDto userDto = UserDto.builder()
                .id(entity.getAuthorId())
                .followersIds(List.of(15L, 16L))
                .build();
        PostPublishedEvent event = new PostPublishedEvent(entity.getId(), userDto.getFollowersIds());
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
        when(postRepository.save(entity)).thenReturn(entity);
        when(userServiceClient.getUser(entity.getAuthorId())).thenReturn(userDto);
        when(mapper.toDto(entity)).thenReturn(dto);

        // when
        PostDto actual = postService.publish(postId);

        // then
        verify(postRepository, times(1)).save(postCaptor.capture());
        Post capturedPost = postCaptor.getValue();
        assertEquals(entity.getId(), capturedPost.getId());
        assertTrue(capturedPost.isPublished());
        assertTrue(Duration.between(currentTime, capturedPost.getPublishedAt()).toSeconds() < 1);
        verify(postRedisService, times(1)).save(entity);
        verify(userRedisService, times(1)).save(userDto);
        verify(kafkaProducer, times(1)).send(postPublishedTopic, event);
        assertEquals(dto, actual);
    }

    @Test
    void testUpdateWithInvalidData() {
        // given
        when(postRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        doThrow(PostWithTwoAuthorsException.class).when(validator).validateBeforeUpdate(dto, entity);

        // then
        assertThrows(PostWithTwoAuthorsException.class, () -> postService.update(dto));
    }

    @Test
    void testUpdateSuccessfully() {
        PostDto dto = PostDto.builder()
                .id(1L)
                .content("new content")
                .build();
        Post entity = Post.builder()
                .id(1L)
                .content("content")
                .build();
        Post updatedEntity = Post.builder()
                .id(1L)
                .content("new content")
                .build();
        when(postRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        when(postRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(mapper.toDto(updatedEntity)).thenReturn(dto);

        PostDto actual = postService.update(dto);

        verify(postRepository, times(1)).save(updatedEntity);
        verify(validator, times(1)).validateBeforeUpdate(dto, updatedEntity);
        verify(postRedisService, times(1)).updateIfExists(updatedEntity);
        assertEquals(dto, actual);
    }

    @Test
    void testDeleteWhenAlreadyDeleted() {
        // given
        Long postId = container.postId();
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
        doThrow(PostAlreadyDeletedException.class).when(validator).validateBeforeDeleting(entity);

        // then
        assertThrows(PostAlreadyDeletedException.class, () -> postService.delete(postId));
    }

    @Test
    void testDelete() {
        // given
        Long postId = container.postId();

        Post entity = Post.builder()
                .id(postId)
                .deleted(false)
                .build();
        Post deletedEntity = Post.builder()
                .id(postId)
                .deleted(true)
                .build();
        PostDto expected = PostDto.builder()
                .id(postId)
                .deleted(true)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
        when(postRepository.save(deletedEntity)).thenReturn(deletedEntity);
        when(mapper.toDto(deletedEntity)).thenReturn(expected);

        // when
        PostDto actual = postService.delete(postId);

        // then
        verify(validator, times(1)).validateBeforeDeleting(entity);
        verify(postRepository, times(1)).save(postCaptor.capture());
        Post capturedEntity = postCaptor.getValue();
        assertEquals(deletedEntity, capturedEntity);
        verify(postRedisService, times(1)).deleteIfExists(postId);
        assertEquals(expected, actual);
    }

    @Test
    void testGetPost() {
        // given
        Long postId = dto.getId();
        long views = 1L;
        PostViewedEvent event = new PostViewedEvent(postId, views);
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
        when(postRepository.incrementAndGetViewsById(postId, 1)).thenReturn(views);
        when(mapper.toDto(entity)).thenReturn(dto);

        // when
        PostDto actual = postService.getPost(postId);

        // then
        verify(postRepository, times(1)).incrementAndGetViewsById(postId, 1);
        verify(kafkaProducer, times(1)).send(postViewedTopic, event);
        assertEquals(dto, actual);
    }

    @Test
    void testFindAllByIdWithLikes() {
        List<Long> ids = List.of(1L, 2L);
        List<Post> posts = List.of(
                Post.builder().id(1L).build(),
                Post.builder().id(2L).build()
        );
        List<PostRedis> postsRedis = List.of(
                PostRedis.builder().id(1L).build(),
                PostRedis.builder().id(2L).build()
        );
        when(postRepository.findAllByIdsWithLikes(ids)).thenReturn(posts);
        when(mapper.toRedis(posts)).thenReturn(postsRedis);

        List<PostRedis> actual = postService.findAllByIdsWithLikes(ids);

        verify(postRepository, times(1)).findAllByIdsWithLikes(ids);
        assertEquals(postsRedis, actual);
    }

    @Test
    void testFindByAuthors() {
        List<Long> authorIds = List.of(1L, 2L);
        int postsCount = 10;
        List<Post> posts = List.of(
                Post.builder().id(1L).authorId(1L).build(),
                Post.builder().id(2L).authorId(1L).build(),
                Post.builder().id(3L).authorId(2L).build()
        );
        List<PostRedis> postsRedis = List.of(
                PostRedis.builder()
                        .id(1L)
                        .author(UserRedis.builder().id(1L).build())
                        .build(),
                PostRedis.builder()
                        .id(2L)
                        .author(UserRedis.builder().id(1L).build())
                        .build(),
                PostRedis.builder()
                        .id(3L)
                        .author(UserRedis.builder().id(2L).build())
                        .build()
        );
        when(postRepository.findByAuthors(authorIds, postsCount)).thenReturn(posts);
        when(mapper.toRedis(posts)).thenReturn(postsRedis);

        List<PostRedis> actual = postService.findByAuthors(authorIds, postsCount);

        verify(postRepository, times(1)).findByAuthors(authorIds, postsCount);
        assertEquals(postsRedis, actual);
    }

    @Test
    void testFindByAuthorsBeforeId() {
        List<Long> authorIds = List.of(1L, 2L);
        int postsCount = 10;
        Long lastPostId = 5L;
        List<Post> posts = List.of(
                Post.builder().id(1L).authorId(1L).build(),
                Post.builder().id(2L).authorId(1L).build(),
                Post.builder().id(3L).authorId(2L).build()
        );
        List<PostRedis> postsRedis = List.of(
                PostRedis.builder()
                        .id(1L)
                        .author(UserRedis.builder().id(1L).build())
                        .build(),
                PostRedis.builder()
                        .id(2L)
                        .author(UserRedis.builder().id(1L).build())
                        .build(),
                PostRedis.builder()
                        .id(3L)
                        .author(UserRedis.builder().id(2L).build())
                        .build()
        );
        when(postRepository.findByAuthorsBeforeId(authorIds, lastPostId, postsCount)).thenReturn(posts);
        when(mapper.toRedis(posts)).thenReturn(postsRedis);

        List<PostRedis> actual = postService.findByAuthorsBeforeId(authorIds, lastPostId, postsCount);

        verify(postRepository, times(1)).findByAuthorsBeforeId(authorIds, lastPostId, postsCount);
        assertEquals(postsRedis, actual);
    }

    @Test
    void testFindPostIdsByFollowerId() {
        Long followerId = 1L;
        int batchSize = 10;
        List<Long> postIds = List.of(1L, 2L, 3L);
        when(postRepository.findPostIdsByFollowerId(followerId, batchSize)).thenReturn(postIds);

        List<Long> actual = postService.findPostIdsByFollowerId(followerId, batchSize);

        verify(postRepository, times(1)).findPostIdsByFollowerId(followerId, batchSize);
        assertEquals(postIds, actual);
    }

    @Test
    void testGetFilteredPosts() {
        // given
        List<Post> posts = createPosts();
        PostFilterDto filters = new PostFilterDto(3L, null, false, false);
        when(postRepository.findAll()).thenReturn(posts);
        int expSize = 1;
        PostDto expDto = mapper.toDto(posts.get(4));

        // when
        List<PostDto> filteredPosts = postService.getFilteredPosts(filters);

        // then
        assertEquals(expSize, filteredPosts.size());
        assertEquals(expDto, filteredPosts.get(0));

    }

    private List<Post> createPosts() {
        Long userId = container.authorId();
        Long projectId = container.projectId();
        boolean isNotDeleted = container.deleted();
        boolean isNotPublished = container.published();
        Long postId = container.postId();
        List<Like> likes = container.likes();

        Post postFirst = createPost(postId++, userId, likes, null, isNotDeleted, isNotPublished, null);
        Post postSecond = createPost(postId++, userId, likes, null, isNotDeleted, !isNotPublished, LocalDateTime.now());
        Post postThird = createPost(postId++, userId, likes, null, !isNotDeleted, isNotPublished, null);
        Post postForth = createPost(postId++, userId++, likes, null, !isNotDeleted, !isNotPublished, LocalDateTime.now());
        Post postFifth = createPost(postId++, userId, likes, null, isNotDeleted, isNotPublished, null);
        Post postSixth = createPost(postId++, null, likes, projectId++, isNotDeleted, isNotPublished, null);
        Post postSeventh = createPost(postId, null, likes, projectId, isNotDeleted, isNotPublished, null);
        return List.of(postFirst, postSecond, postThird, postForth, postFifth, postSixth, postSeventh);
    }

    private Post createPost(Long postId, Long authorId, List<Like> likes, Long projectId, boolean deleted, boolean published, LocalDateTime publishedAt) {
        return Post.builder()
                .id(postId)
                .authorId(authorId)
                .projectId(projectId)
                .deleted(deleted)
                .published(published)
                .publishedAt(publishedAt)
                .likes(likes)
                .build();
    }

}
