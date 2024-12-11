package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModerationSchedulerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private ModerationScheduler moderationScheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(moderationScheduler, "numOfChunk", 2);
    }

    @Test
    void testVerifyPosts_ShouldNotCallVerifyPostAsync() {
        when(postService.findNotReviewedPost()).thenReturn(new ArrayList<>());
        moderationScheduler.verifyPosts();

        verify(postService, never()).verifyPostAsync(any());
    }

    @Test
    void testVerifyPosts_ShouldCallVerifyPostAsync() {
        List<Post> posts = List.of(new Post(), new Post(), new Post(), new Post());
        when(postService.findNotReviewedPost()).thenReturn(posts);

        moderationScheduler.verifyPosts();

        verify(postService, times(2)).verifyPostAsync(anyList());
    }

    @Test
    void testVerifyPosts_WhenPostsNotDivisibleByChunks_ShouldHandleRemainder() {
        List<Post> posts = List.of(new Post(), new Post(), new Post());
        when(postService.findNotReviewedPost()).thenReturn(posts);

        moderationScheduler.verifyPosts();

        verify(postService, times(2)).verifyPostAsync(anyList());
    }

    @Test
    void testVerifyPosts_WhenSinglePost_ShouldCreateSingleChunk() {
        List<Post> posts = List.of(new Post());
        when(postService.findNotReviewedPost()).thenReturn(posts);

        moderationScheduler.verifyPosts();

        verify(postService, times(1)).verifyPostAsync(anyList());
    }
}
