package faang.school.postservice.service.post;

import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.post.PostPublishedException;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.RedisMessagePublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static faang.school.postservice.model.VerificationPostStatus.REJECTED;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostValidator postValidator;
    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @InjectMocks
    private PostService postService;

    private final List<Post> posts = new ArrayList<>();
    private Post postForCreate;
    private Post postForUpdate;
    private Post findedPost;
    private List<Post> authorPosts = new ArrayList<>();
    private List<Post> projectPosts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        postForCreate = Post.builder()
                .content("Some Content")
                .authorId(1L)
                .build();

        postForUpdate = Post.builder()
                .id(1L)
                .content("Updated Content")
                .authorId(2L)
                .build();

        findedPost = Post.builder()
                .id(1L)
                .content("Some Content")
                .authorId(1L)
                .build();

        authorPosts.add(Post.builder()
                .id(1L)
                .content("Content 1")
                .deleted(false)
                .published(false)
                .authorId(1L)
                .createdAt(LocalDateTime.of(2024, 9, 17, 0, 0))
                .publishedAt(LocalDateTime.of(2024, 9, 17, 0, 0))
                .build());

        authorPosts.add(Post.builder()
                .id(2L)
                .content("Content 2")
                .deleted(false)
                .published(true)
                .authorId(2L)
                .createdAt(LocalDateTime.of(2024, 9, 16, 0, 0))
                .publishedAt(LocalDateTime.of(2024, 9, 16, 0, 0))
                .build());

        projectPosts.add(Post.builder()
                .id(3L)
                .content("Content 3")
                .deleted(false)
                .published(true)
                .projectId(1L)
                .createdAt(LocalDateTime.of(2024, 9, 13, 0, 0))
                .publishedAt(LocalDateTime.of(2024, 9, 13, 0, 0))
                .build());

        projectPosts.add(Post.builder()
                .id(4L)
                .content("Content 4")
                .deleted(false)
                .published(true)
                .projectId(1L)
                .createdAt(LocalDateTime.of(2024, 9, 14, 0, 0))
                .publishedAt(LocalDateTime.of(2024, 9, 14, 0, 0))
                .build());
    }

    @Test
    void testSuccessCreate() {
        when(postRepository.save(postForCreate)).thenReturn(postForCreate);

        Post result = postService.create(postForCreate);

        verify(postRepository).save(postForCreate);

        assertEquals(result.getContent(), postForCreate.getContent());
        assertFalse(result.isDeleted());
        assertFalse(result.isPublished());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void testSuccessUpdate() {
        when(postRepository
                .findByIdAndNotDeleted(postForUpdate.getId()))
                .thenReturn(Optional.ofNullable(findedPost));

        Post result = postService.update(postForUpdate);

        verify(postRepository).save(any(Post.class));

        assertEquals(result.getContent(), postForUpdate.getContent());
        assertNotNull(result.getUpdatedAt());
        assertNotEquals(result.getAuthorId(), postForUpdate.getAuthorId());
    }

    @Test
    void testSuccessPublishPost() {
        when(postRepository
                .findByIdAndNotDeleted(postForUpdate.getId()))
                .thenReturn(Optional.ofNullable(findedPost));

        Post result = postService.publish(findedPost.getId());

        verify(postRepository).save(any(Post.class));

        assertTrue(result.isPublished());
        assertNotNull(result.getPublishedAt());
    }

    @Test
    void testFailedPublishPost() {
        findedPost.setPublished(true);
        when(postRepository
                .findByIdAndNotDeleted(postForUpdate.getId()))
                .thenReturn(Optional.ofNullable(findedPost));

        assertThrows(PostPublishedException.class, () -> postService.publish(findedPost.getId()));
        verify(postRepository, times(0)).save(any(Post.class));
    }

    @Test
    void testSuccessDelete() {
        when(postRepository
                .findByIdAndNotDeleted(1L))
                .thenReturn(Optional.ofNullable(findedPost));

        postService.delete(findedPost.getId());

        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testFindPostById() {
        when(postRepository
                .findByIdAndNotDeleted(findedPost.getId()))
                .thenReturn(Optional.ofNullable(findedPost));

        Post result = postService.findPostById(findedPost.getId());

        verify(postRepository).findByIdAndNotDeleted(findedPost.getId());
        assertEquals(result, findedPost);
    }

    @Test
    void testFindPostByIdNotFound() {
        when(postRepository
                .findByIdAndNotDeleted(findedPost.getId()))
                .thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findPostById(findedPost.getId()));

        verify(postRepository).findByIdAndNotDeleted(findedPost.getId());
    }

    @Test
    void testSearchPublishedPostsByAuthor() {
        Post filterPost = Post.builder()
                .authorId(2L)
                .published(true)
                .build();

        when(postRepository
                .findByAuthorId(filterPost.getAuthorId()))
                .thenReturn(authorPosts.stream()
                        .filter((p) -> p.getAuthorId().equals(filterPost.getAuthorId()))
                        .toList()
                );

        List<Post> result = postService.searchByAuthor(filterPost);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), authorPosts.get(1));

        verify(postRepository).findByAuthorId(filterPost.getAuthorId());
        verify(postRepository, times(0)).findByProjectId(anyLong());
    }

    @Test
    void testSearchUnPublishedPostsByAuthor() {
        Post filterPost = Post.builder()
                .authorId(1L)
                .published(false)
                .build();

        when(postRepository
                .findByAuthorId(filterPost.getAuthorId()))
                .thenReturn(authorPosts.stream()
                        .filter((p) -> p.getAuthorId().equals(filterPost.getAuthorId()))
                        .toList()
                );

        List<Post> result = postService.searchByAuthor(filterPost);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), authorPosts.get(0));
        assertFalse(result.get(0).isPublished());

        verify(postRepository).findByAuthorId(filterPost.getAuthorId());
        verify(postRepository, times(0)).findByProjectId(anyLong());
    }

    @Test
    void testSearchPublishedPostsByProject() {
        Post filterPost = Post.builder()
                .projectId(1L)
                .published(true)
                .build();

        when(postRepository
                .findByProjectId(filterPost.getProjectId()))
                .thenReturn(projectPosts.stream()
                        .filter((p) -> p.getProjectId().equals(filterPost.getProjectId()))
                        .toList()
                );

        List<Post> result = postService.searchByProject(filterPost);

        assertEquals(result.size(), 2);
        assertEquals(result.get(0), projectPosts.get(1));
        assertEquals(result.get(1), projectPosts.get(0));

        verify(postRepository).findByProjectId(filterPost.getProjectId());
        verify(postRepository, times(0)).findByAuthorId(anyLong());
    }

    @Test
    void testSearchUnPublishedPostsByProject() {
        Post filterPost = Post.builder()
                .projectId(1L)
                .published(false)
                .build();

        when(postRepository
                .findByProjectId(filterPost.getProjectId()))
                .thenReturn(projectPosts.stream()
                        .filter((p) -> p.getProjectId().equals(filterPost.getProjectId()))
                        .toList()
                );

        List<Post> result = postService.searchByProject(filterPost);

        assertEquals(result.size(), 0);

        verify(postRepository).findByProjectId(filterPost.getProjectId());
        verify(postRepository, times(0)).findByAuthorId(anyLong());
    }

    @Test
    void testPublishingUsersForBanWhenAllUsersHasMoreThan5RejectedPosts() {
        addPostsToList(1, 6, 1);
        addPostsToList(7, 6, 2);
        addPostsToList(13, 6, 3);
        addPostsToList(20, 7, 4);
        addPostsToList(27, 7, 5);
        when(postRepository.findAllByVerificationStatus(REJECTED)).thenReturn(posts);

        postService.publishingUsersForBan();

        verify(redisMessagePublisher, times(1)).publish(String.valueOf(1));
        verify(redisMessagePublisher, times(1)).publish(String.valueOf(2));
        verify(redisMessagePublisher, times(1)).publish(String.valueOf(3));
        verify(redisMessagePublisher, times(1)).publish(String.valueOf(4));
        verify(redisMessagePublisher, times(1)).publish(String.valueOf(5));
    }

    @Test
    void testPublishingUsersForBanWhenSomeUsersHasMoreThan5RejectedPosts() {
        addPostsToList(1, 6, 1);
        addPostsToList(7, 6, 2);
        addPostsToList(13, 5, 3);
        addPostsToList(19, 5, 4);
        addPostsToList(25, 6, 5);
        when(postRepository.findAllByVerificationStatus(REJECTED)).thenReturn(posts);

        postService.publishingUsersForBan();

        verify(redisMessagePublisher, times(1)).publish(String.valueOf(1));
        verify(redisMessagePublisher, times(1)).publish(String.valueOf(2));
        verify(redisMessagePublisher, times(0)).publish(String.valueOf(3));
        verify(redisMessagePublisher, times(0)).publish(String.valueOf(4));
        verify(redisMessagePublisher, times(1)).publish(String.valueOf(5));
    }

    private void addPostsToList(long from, int quantity, long userId) {
        LongStream.rangeClosed(from, from + quantity - 1)
                .mapToObj(id -> Post.builder()
                        .id(id)
                        .authorId(userId)
                        .verificationStatus(REJECTED)
                        .build())
                .forEach(posts::add);
    }
}
