package faang.school.postservice.scheduler;

import faang.school.postservice.api.PostCorrector;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorrectorSchedulerTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCorrector postCorrector;

    @InjectMocks
    private CorrectorScheduler correctorScheduler;

    @Test
    @DisplayName("Correct posts")
    void correctorSchedulerTest_correctPosts() {
        List<Post> posts = List.of(
                Post.builder().build(),
                Post.builder().build());
        when(postRepository.findAllNotPublishedPosts()).thenReturn(posts);
        when(postCorrector.correctPost(any(Post.class))).thenReturn("corrected");

        correctorScheduler.correctNotPublishedPostsContent();

        verify(postRepository).findAllNotPublishedPosts();
        verify(postCorrector, times(posts.size())).correctPost(any(Post.class));
        verify(postRepository).saveAll(posts);
        assertTrue(posts.stream().allMatch(post -> post.getContent().equals("corrected")));
    }

    @Test
    @DisplayName("Correct empty list of posts")
    void correctorSchedulerTest_correctEmptyListOfPosts() {
        List<Post> posts = new ArrayList<>();
        when(postRepository.findAllNotPublishedPosts()).thenReturn(posts);

        correctorScheduler.correctNotPublishedPostsContent();

        assertTrue(posts.isEmpty());
        verify(postRepository).findAllNotPublishedPosts();
        verify(postCorrector, times(0)).correctPost(any(Post.class));
        verify(postRepository).saveAll(posts);
    }

    @Test
    @DisplayName("Correct posts with exception")
    void correctorSchedulerTest_correctPostsWithException() {
        when(postRepository.saveAll(any())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> correctorScheduler.correctNotPublishedPostsContent());
    }
}
