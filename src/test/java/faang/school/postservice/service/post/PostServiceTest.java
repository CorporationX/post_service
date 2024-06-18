package faang.school.postservice.service.post;

import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.dto.event.UserEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.postview.PostViewEventPublisher;
import faang.school.postservice.publisher.userban.UserBanPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validation.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostValidator postValidator;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private ResourceService resourceService;
    @Mock
    private UserBanPublisher userBanPublisher;
    @Mock
    private PostViewEventPublisher postViewEventPublisher;
    private ExecutorService threadPool;
    private PostService postService;
    private RedisPostRepository redisPostRepository;

    private Post firstPost;
    private Post secondPost;
    private Post thirdPost;
    private PostDto firstPostDto;
    private UserEvent userBanMessage;

    @BeforeEach
    void setUp() {
        firstPost = Post.builder()
                .id(1L)
                .content("Valid content")
                .authorId(1L)
                .resources(new ArrayList<>())
                .published(false)
                .build();
        secondPost = Post.builder()
                .id(2L)
                .content("Valid content")
                .resources(new ArrayList<>())
                .authorId(1L)
                .published(false)
                .build();
        thirdPost = Post.builder()
                .id(3L)
                .content("Valid content")
                .resources(new ArrayList<>())
                .authorId(1L)
                .published(false)
                .build();
        firstPostDto = PostDto.builder()
                .id(firstPost.getId())
                .content(firstPost.getContent())
                .authorId(firstPost.getAuthorId())
                .build();
        userBanMessage = UserEvent.builder()
                .userId(firstPost.getAuthorId())
                .build();
        threadPool = Executors.newFixedThreadPool(10);
        postService = new PostService(postRepository, postValidator, postMapper, resourceService, threadPool,
                userBanPublisher, postViewEventPublisher, redisPostRepository);
    }

    @Test
    void create_PostCreated_ThenReturnedAsDto() {
        when(postRepository.save(any(Post.class))).thenReturn(firstPost);

        PostDto returned = postService.create(firstPostDto, new MultipartFile[]{});

        assertAll(
                () -> verify(postValidator, times(1)).validatePostAuthor(firstPostDto),
                () -> verify(postValidator, times(1)).validateIfAuthorExists(firstPostDto),
                () -> verify(postValidator, times(1)).validateResourcesCount(anyInt()),
                () -> verify(postRepository, times(1)).save(any(Post.class)),
                () -> verify(postMapper, times(1)).toEntity(firstPostDto),
                () -> verify(postMapper, times(1)).toDto(firstPost),
                () -> assertEquals(firstPostDto, returned)
        );
    }

    @Test
    void getPostById_PostFound_thenReturnedAsDto() {
        when(postRepository.findById(firstPost.getId())).thenReturn(Optional.ofNullable(firstPost));

        PostDto returned = postService.getPostById(1L, firstPost.getId());

        assertAll(
                () -> verify(postRepository, times(1)).findById(firstPost.getId()),
                () -> verify(postMapper, times(1)).toDto(firstPost),
                () -> verify(postViewEventPublisher, never()).publish(any(PostViewEvent.class)),
                () -> assertEquals(firstPostDto, returned)
        );
    }

    @Test
    void getPostById_PostNotFound_ShouldThrowEntityNotFoundException() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> postService.getPostById(1L, 1299L));
    }

    @Test
    void publish_PostPublished_ThenReturnedAsDto() {
        when(postRepository.findById(firstPost.getId())).thenReturn(Optional.ofNullable(firstPost));
        when(postRepository.save(firstPost)).thenReturn(firstPost);

        PostDto returned = postService.publish(firstPost.getId());

        assertAll(
                () -> verify(postRepository, times(1)).findById(firstPost.getId()),
                () -> verify(postRepository, times(1)).save(firstPost),
                () -> verify(postMapper, times(1)).toDto(firstPost),
                () -> assertTrue(returned.isPublished()),
                () -> assertNotNull(returned.getPublishedAt()),
                () -> assertNotEquals(firstPostDto, returned)
        );
    }

    @Test
    void publishScheduledPosts() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        List<Post> posts = new ArrayList<>(List.of(firstPost, secondPost, thirdPost));
        Field batchSize = postService.getClass().getDeclaredField("scheduledPostsBatchSize");
        batchSize.setAccessible(true);
        batchSize.set(postService, 1000);
        when(postRepository.findReadyToPublish()).thenReturn(posts);

        postService.publishScheduledPosts();

        threadPool.shutdown();
        threadPool.awaitTermination(5L, TimeUnit.MINUTES);
        assertAll(
                () -> verify(postRepository, times(1)).findReadyToPublish(),
                () -> assertEquals(List.of(true, true, true), posts.stream().map(Post::isPublished).toList()),
                () -> assertNotNull(firstPost.getPublishedAt()),
                () -> assertNotNull(secondPost.getPublishedAt()),
                () -> assertNotNull(thirdPost.getPublishedAt())
        );
    }

    @Test
    void update_PostUpdated_ThenReturnedAsDto() {
        firstPost.setContent("Old content");
        firstPostDto.setContent("Updated content");
        when(postRepository.findById(firstPost.getId())).thenReturn(Optional.ofNullable(firstPost));

        PostDto returned = postService.update(firstPostDto, new MultipartFile[]{});

        assertAll(
                () -> verify(postRepository, times(1)).findById(firstPost.getId()),
                () -> verify(postValidator, times(1)).validateUpdatedPost(any(Post.class), any(PostDto.class)),
                () -> verify(postValidator, times(1)).validateResourcesCount(anyInt(), anyInt()),
                () -> verify(postMapper, times(1)).toDto(firstPost),
                () -> assertEquals(firstPostDto, returned),
                () -> assertNotEquals("Old content", firstPost.getContent())
        );
    }

    @Test
    void banUsers_UserShouldBeBanned_BanMessagePublished() throws NoSuchFieldException, IllegalAccessException {
        firstPost.setVerified(false);
        secondPost.setVerified(false);
        List<Post> posts = List.of(firstPost, secondPost, thirdPost);
        Field postsCountToBan = PostService.class.getDeclaredField("postsCountToBan");
        postsCountToBan.setAccessible(true);
        postsCountToBan.set(postService, 2);
        when(postRepository.findByVerifiedFalse()).thenReturn(posts);

        postService.banUsers();

        assertAll(
                () -> verify(postRepository, times(1)).findByVerifiedFalse(),
                () -> verify(userBanPublisher, times(1)).publish(userBanMessage)
        );
    }

    @Test
    void delete_PostWasMarkedAsDeleted_ThenSavedToDb() {
        when(postRepository.findById(firstPost.getId())).thenReturn(Optional.ofNullable(firstPost));
        when(postRepository.save(firstPost)).thenReturn(firstPost);

        postService.delete(firstPost.getId());

        assertAll(
                () -> verify(postRepository, times(1)).findById(firstPost.getId()),
                () -> verify(postRepository, times(1)).save(firstPost),
                () -> assertTrue(firstPost.isDeleted())
        );
    }

    @Test
    void getCreatedPostsByAuthorId_PostsFilteredAndSorted_ThenReturnedAsDto() {
        setPostsCreationDates();
        firstPost.setPublished(true);
        when(postRepository.findByAuthorId(anyLong())).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> returned = postService.getCreatedPostsByAuthorId(1L);

        assertAll(
                () -> verify(postRepository, times(1)).findByAuthorId(1L),
                () -> verify(postMapper, times(1)).toDto(List.of(thirdPost, secondPost)),
                () -> assertEquals(2, returned.size()),
                () -> assertEquals(postMapper.toDto(thirdPost), returned.get(0))
        );
    }

    @Test
    void getCreatedPostsByProjectId_PostsFilteredAndSorted_ThenReturnedAsDto() {
        setProjectInsteadOfAuthor();
        setPostsCreationDates();
        firstPost.setPublished(true);
        when(postRepository.findByProjectId(anyLong())).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> returned = postService.getCreatedPostsByProjectId(2L);

        assertAll(
                () -> verify(postRepository, times(1)).findByProjectId(2L),
                () -> verify(postMapper, times(1)).toDto(List.of(thirdPost, secondPost)),
                () -> assertEquals(2, returned.size()),
                () -> assertEquals(postMapper.toDto(secondPost), returned.get(1))
        );
    }

    @Test
    void getPublishedPostsByAuthorId_PostsFilteredAndSorted_ThenReturnedAsDto() {
        setPublishedForAllPosts();
        setPostsPublishDates();
        thirdPost.setDeleted(true);
        when(postRepository.findByAuthorId(anyLong())).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> returned = postService.getPublishedPostsByAuthorId(1L, 1L);

        assertAll(
                () -> verify(postRepository, times(1)).findByAuthorId(1L),
                () -> verify(postMapper, times(1)).toDto(List.of(secondPost, firstPost)),
                () -> verify(postViewEventPublisher, times(2)).publish(any(PostViewEvent.class)),
                () -> assertEquals(2, returned.size()),
                () -> assertEquals(postMapper.toDto(secondPost), returned.get(0))
        );
    }

    @Test
    void getPublishedPostsByProjectId_PostsFilteredAndSorted_ThenReturnedAsDto() {
        setPublishedForAllPosts();
        setPostsPublishDates();
        setProjectInsteadOfAuthor();
        thirdPost.setDeleted(true);
        when(postRepository.findByProjectId(anyLong())).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> returned = postService.getPublishedPostsByProjectId(1L, 2L);

        assertAll(
                () -> verify(postRepository, times(1)).findByProjectId(2L),
                () -> verify(postMapper, times(1)).toDto(List.of(secondPost, firstPost)),
                () -> verify(postViewEventPublisher, times(2)).publish(any(PostViewEvent.class)),
                () -> assertEquals(2, returned.size()),
                () -> assertEquals(postMapper.toDto(firstPost), returned.get(1))
        );
    }

    private void setPostsCreationDates() {
        firstPost.setCreatedAt(LocalDateTime.now().minusDays(10));
        secondPost.setCreatedAt(LocalDateTime.now().minusDays(7));
        thirdPost.setCreatedAt(LocalDateTime.now().minusDays(2));
    }

    private void setPostsPublishDates() {
        firstPost.setPublishedAt(LocalDateTime.now().minusDays(10));
        secondPost.setPublishedAt(LocalDateTime.now().minusDays(7));
        thirdPost.setPublishedAt(LocalDateTime.now().minusDays(2));
    }

    private void setProjectInsteadOfAuthor() {
        firstPost.setAuthorId(null);
        firstPost.setProjectId(2L);
        secondPost.setAuthorId(null);
        secondPost.setProjectId(2L);
        thirdPost.setAuthorId(null);
        thirdPost.setProjectId(2L);
    }

    private void setPublishedForAllPosts() {
        firstPost.setPublished(true);
        secondPost.setPublished(true);
        thirdPost.setPublished(true);
    }
}
