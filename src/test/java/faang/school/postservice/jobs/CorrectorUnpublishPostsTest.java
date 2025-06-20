package faang.school.postservice.jobs;

import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.post.PostCorrecter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorrectorUnpublishPostsTest {

    @Mock
    private PostCorrecter postCorrecter;

    @Mock
    private PostService postService;

    @InjectMocks
    private CorrectorUnpublishPosts correctorUnpublishPosts;

    private List<Post> posts;
    private Post testPost;

    @BeforeEach
    void setUp() {
        posts = new ArrayList<>();
        testPost = Post.builder()
                .id(1L)
                .content("content")
                .build();
        posts.add(testPost);
    }

    @Test
    void testCorrectingSpellingOfPostsWhenWorkingJob() {
        when(postService.getAllUnpublishedPost()).thenReturn(posts);
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        when(postCorrecter.correctingContentPost(testPost)).thenReturn(future);

        correctorUnpublishPosts.correctingSpellingOfPosts();


        verify(postService).getAllUnpublishedPost();
        verify(postCorrecter).correctingContentPost(testPost);
    }

    @Test
    void testCorrectingSpellingOfPostsWhenEmptyPostList() {
        when(postService.getAllUnpublishedPost()).thenReturn(List.of());

        correctorUnpublishPosts.correctingSpellingOfPosts();

        verify(postService).getAllUnpublishedPost();
    }
}
