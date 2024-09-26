package faang.school.postservice.service.post;

import faang.school.postservice.config.redis.GeneralRedisConfig;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.messaging.publisher.post.PostEventPublisher;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapperImpl postMapper = new PostMapperImpl();

    @Mock
    private PostValidator postValidator;
    @Mock
    private PostEventPublisher postEventPublisher;
    @Mock
    private GeneralRedisConfig redisConfig;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    private PostDto postDto;
    private Post post;
    private final long defaultIdForTests = 1;

    @BeforeEach
    void setUp() {
        postDto = new PostDto();
        post = new Post();
    }

    @Test
    void testCreateWhenAuthorNotExists() {
        when(postValidator.checkIfAuthorExists(postDto)).thenReturn(false);

        assertThrows(PostValidationException.class, () -> postService.create(postDto));
    }

    @Test
    void testCreateWhenAuthorExists() {

        when(postValidator.checkIfAuthorExists(postDto)).thenReturn(true);
        when(postRepository.save(post)).thenReturn(post);

        PostDto returnedPostDto = postService.create(postDto);

        assertEquals(postDto, returnedPostDto);
    }

    @Test
    void testPublish() {
        Optional<Post> postOptional = Optional.of(post);

        when(postRepository.findById(defaultIdForTests)).thenReturn(postOptional);
        doNothing().when(postValidator).validatePublish(postOptional);
        doNothing().when(postEventPublisher).publish(any(PostEvent.class));
        when(postRepository.save(post)).thenReturn(post);

        when(redisConfig.getPostCacheTtl()).thenReturn(86400L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        PostDto returnedPostDto = postService.publish(defaultIdForTests);

        assertEquals(postDto, returnedPostDto);

        verify(redisTemplate.opsForValue(), times(1)).set(
                eq(String.valueOf(defaultIdForTests)),
                eq(postDto),
                eq(86400L),
                eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void testUpdate() {
        doNothing().when(postValidator).validateUpdate(defaultIdForTests, postDto);
        when(postRepository.save(any())).thenReturn(post);

        PostDto returnedPostDto = postService.update(defaultIdForTests, postDto);

        assertEquals(postDto, returnedPostDto);
    }

    @Test
    void testSoftDeleteWhenPostNotExists() {
        Optional<Post> postOptional = Optional.empty();

        when(postRepository.findById(defaultIdForTests)).thenReturn(postOptional);

        assertThrows(PostValidationException.class, () -> postService.softDelete(defaultIdForTests));
    }

    @Test
    void testSoftDeleteWhenPostExists() {
        Optional<Post> postOptional = Optional.of(post);

        when(postRepository.findById(defaultIdForTests)).thenReturn(postOptional);
        when(postRepository.save(post)).thenReturn(post);

        PostDto returnedPostDto = postService.softDelete(defaultIdForTests);

        assertEquals(postDto, returnedPostDto);
    }

    @Test
    void testGetByIdWhenPostNotExists() {
        Optional<Post> postOptional = Optional.empty();

        when(postRepository.findById(defaultIdForTests)).thenReturn(postOptional);

        assertThrows(PostValidationException.class, () -> postService.getById(defaultIdForTests));
    }

    @Test
    void testGetByIdWhenPostExists() {
        Optional<Post> postOptional = Optional.of(post);

        when(postRepository.findById(defaultIdForTests)).thenReturn(postOptional);

        PostDto returnedPostDto = postService.getById(defaultIdForTests);

        assertEquals(postDto, returnedPostDto);
    }

    @Test
    void testGetAllUnpublishedPostsForAuthorWhenNoPosts() {
        when(postRepository.findByAuthorId(defaultIdForTests)).thenReturn(null);

        assertThrows(PostValidationException.class, () -> postService.getAllUnpublishedPostsForAuthor(defaultIdForTests));
    }

    @Test
    void testGetAllUnpublishedPostsForAuthorWhenPostsExists() {
        List<Post> posts = getPosts();
        List<PostDto> expectedPostsDto = getPostsDto(posts, post -> !post.isPublished() && !post.isDeleted(),
                Comparator.comparing(Post::getCreatedAt).reversed());

        when(postRepository.findByAuthorId(defaultIdForTests)).thenReturn(posts);

        List<PostDto> returnedPostsDto = postService.getAllUnpublishedPostsForAuthor(defaultIdForTests);

        assertEquals(expectedPostsDto, returnedPostsDto);
    }

    @Test
    void testGetAllUnpublishedPostsForProjectWhenNoPosts() {
        when(postRepository.findByProjectId(defaultIdForTests)).thenReturn(null);

        assertThrows(PostValidationException.class, () -> postService.getAllUnpublishedPostsForProject(defaultIdForTests));
    }

    @Test
    void testGetAllUnpublishedPostsForProjectWhenPostsExists() {
        List<Post> posts = getPosts();
        List<PostDto> expectedPostsDto = getPostsDto(posts, post -> !post.isPublished() && !post.isDeleted(),
                Comparator.comparing(Post::getCreatedAt).reversed());

        when(postRepository.findByProjectId(defaultIdForTests)).thenReturn(posts);

        List<PostDto> returnedPostsDto = postService.getAllUnpublishedPostsForProject(defaultIdForTests);

        assertEquals(expectedPostsDto, returnedPostsDto);
    }

    @Test
    void testGetAllPublishedPostsForAuthorWhenNoPosts() {
        when(postRepository.findByAuthorId(defaultIdForTests)).thenReturn(null);

        assertThrows(PostValidationException.class, () -> postService.getAllPublishedPostsForAuthor(defaultIdForTests));
    }

    @Test
    void testGetAllPublishedPostsForAuthorWhenPostsExists() {
        List<Post> posts = getPosts();
        List<PostDto> expectedPostsDto = getPostsDto(posts, post -> post.isPublished() && !post.isDeleted(),
                Comparator.comparing(Post::getCreatedAt).reversed());

        when(postRepository.findByAuthorId(defaultIdForTests)).thenReturn(posts);

        List<PostDto> returnedPostsDto = postService.getAllPublishedPostsForAuthor(defaultIdForTests);

        assertEquals(expectedPostsDto, returnedPostsDto);
    }

    @Test
    void testGetAllPublishedPostsForProjectWhenNoPosts() {
        when(postRepository.findByProjectId(defaultIdForTests)).thenReturn(null);

        assertThrows(PostValidationException.class, () -> postService.getAllPublishedPostsForProject(defaultIdForTests));
    }

    @Test
    void testGetAllPublishedPostsForProjectWhenPostExists() {
        List<Post> posts = getPosts();
        List<PostDto> expectedPostsDto = getPostsDto(posts, post -> post.isPublished() && !post.isDeleted(),
                Comparator.comparing(Post::getCreatedAt).reversed());

        when(postRepository.findByProjectId(defaultIdForTests)).thenReturn(posts);

        List<PostDto> returnedPostsDto = postService.getAllPublishedPostsForProject(defaultIdForTests);

        assertEquals(expectedPostsDto, returnedPostsDto);
    }

    private List<Post> getPosts() {
        Post postFirst = new Post();
        Post postSecond = new Post();
        postFirst.setCreatedAt(LocalDateTime.MIN);
        postSecond.setCreatedAt(LocalDateTime.MAX);

        return List.of(postFirst, postSecond);
    }

    private List<PostDto> getPostsDto(List<Post> posts, Predicate<Post> predicate, Comparator<Post> comparator) {
        return posts.stream()
                .filter(predicate)
                .sorted(comparator)
                .map(postMapper::toDto)
                .toList();
    }
}