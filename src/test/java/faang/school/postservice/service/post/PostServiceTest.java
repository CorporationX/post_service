package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.WrongTimeException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private Post post;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;
    private PostDto postDto;
    private Post postEntity;

    @Mock
    private PostMapper postMapper;

    @Mock
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .content("Test post")
                .build();

        postDto = PostDto.builder()
                .id(1L)
                .content("Test post")
                .authorId(123L)
                .projectId(456L)
                .build();
        postEntity = Post.builder()
                .id(1L)
                .content("Test post")
                .authorId(123L)
                .projectId(456L)
                .build();
    }

    @Test
    void getPostByIdValidIdShouldReturnPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Post result = postService.getPostById(1L);
        assertEquals(post, result);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void getPostByIdInvalidIdShouldThrowEntityNotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(1L));
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void publishScheduledPostsShouldNotPublishIfNoPosts() {
        when(postRepository.findReadyToPublish()).thenReturn(List.of());
        postService.publishScheduledPosts();
        verify(postRepository, never()).saveAll(anyList());
    }

    @Test
    void publishScheduledPostsShouldCallFindReadyToPublishAndPublishPostBatch() {
        List<Post> readyToPublishPosts = List.of(post);
        when(postRepository.findReadyToPublish()).thenReturn(readyToPublishPosts);
        postService.publishScheduledPosts();
        verify(postRepository, times(1)).findReadyToPublish();
        verify(executorService, times(1)).execute(any(Runnable.class));
    }

    @Test
    void publishScheduledPostsShouldNotCallPublishPostBatchIfNoPostsToPublish() {
        when(postRepository.findReadyToPublish()).thenReturn(new ArrayList<>());
        postService.publishScheduledPosts();
        verify(postRepository, times(1)).findReadyToPublish();
        verify(executorService, never()).execute(any(Runnable.class));
    }

    @Test
    void publishScheduledPosts_shouldPartitionPostsIntoBatches() {
        List<Post> readyToPublishPosts = new ArrayList<>();
        for (int i = 0; i < 1500; i++) {
            readyToPublishPosts.add(Post.builder()
                    .id((long) i + 1)
                    .content("Test post " + i)
                    .scheduledAt(LocalDateTime.now().plusMinutes(1))
                    .build());
        }
        when(postRepository.findReadyToPublish()).thenReturn(readyToPublishPosts);
        postService.publishScheduledPosts();
        verify(executorService, times(2)).execute(any(Runnable.class));
    }

    @Test
    void publishScheduledPostsShouldHandleExceptionFromPublishPostBatch() {
        List<Post> readyToPublishPosts = List.of(post);
        when(postRepository.findReadyToPublish()).thenReturn(readyToPublishPosts);
        doThrow(new RuntimeException("Error publishing posts")).when(executorService).execute(any(Runnable.class));
        assertThrows(RuntimeException.class, () -> postService.publishScheduledPosts());
    }

    @Test
    void createPostShouldSavePostWithCorrectFieldsWhenScheduledAtIsNull() {
        when(postMapper.toEntity(postDto)).thenReturn(postEntity);
        when(postRepository.save(postEntity)).thenReturn(postEntity);
        Post createdPost = postService.createPost(postDto);
        verify(postMapper, times(1)).toEntity(postDto);
        verify(postRepository, times(1)).save(postEntity);
        assertTrue(createdPost.isPublished());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                createdPost.getPublishedAt().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void createPostShouldSavePostWithCorrectFieldsWhenScheduledAtIsNotNull() {
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(1);
        postDto.setScheduledAt(scheduledAt);
        when(postMapper.toEntity(postDto)).thenReturn(postEntity);
        when(postRepository.save(postEntity)).thenReturn(postEntity);
        Post createdPost = postService.createPost(postDto);
        verify(postMapper, times(1)).toEntity(postDto);
        verify(postRepository, times(1)).save(postEntity);
        assertFalse(createdPost.isPublished());
        assertEquals(scheduledAt, createdPost.getPublishedAt());
    }

    @Test
    void createPostShouldThrowWrongTimeExceptionWhenScheduledAtIsInPast() {
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        postDto.setScheduledAt(pastTime);
        assertThrows(WrongTimeException.class, () -> postService.createPost(postDto));
        verify(postRepository, never()).save(postEntity);
    }

    @Test
    void whenExistsByIdThenTrue() {
        when(postRepository.existsById(anyLong())).thenReturn(true);
        assertTrue(postService.existsById(1));
    }

    @Test
    void whenExistsByIdThenFalse() {
        when(postRepository.existsById(anyLong())).thenReturn(false);
        assertFalse(postService.existsById(1));
    }
}