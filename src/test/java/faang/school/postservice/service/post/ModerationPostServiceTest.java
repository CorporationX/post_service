package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerificationPostStatus;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ModerationPostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private ModerationAsyncService moderationAsyncService;

    @InjectMocks
    private ModerationPostService moderationPostService;

    private List<Post> posts;
    private Post postOne;
    private Post postTwo;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        postOne = Post.builder()
                .content("This is post content with badword.")
                .authorId(1L)
                .projectId(1L)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .deleted(false)
                .verificationStatus(VerificationPostStatus.UNVERIFIED)
                .build();

        postTwo = Post.builder()
                .content("Content.")
                .authorId(2L)
                .projectId(1L)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .deleted(false)
                .verificationStatus(VerificationPostStatus.UNVERIFIED)
                .build();

        posts = new ArrayList<>();
        posts.add(postOne);
        posts.add(postTwo);

        Field sublistSizeField = ModerationPostService.class.getDeclaredField("sublistSize");
        sublistSizeField.setAccessible(true);
        sublistSizeField.set(moderationPostService, 2);
    }

    @Test
    void testModeratePostsSublist() {
        when(postRepository.findUnverifiedPosts()).thenReturn(posts);

        moderationPostService.moderateUnverifiedPosts();

        verify(moderationAsyncService, times(1)).moderatePostsSublistAsync(posts);
    }

    @Test
    void testModerateUnverifiedPosts() {
        Post postThree = Post.builder()
                .content("Another unverified post.")
                .authorId(3L)
                .projectId(1L)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .deleted(false)
                .verificationStatus(VerificationPostStatus.UNVERIFIED)
                .build();

        List<Post> unverifiedPosts = Arrays.asList(postOne, postTwo, postThree);

        when(postRepository.findUnverifiedPosts()).thenReturn(unverifiedPosts);

        moderationPostService.moderateUnverifiedPosts();

        verify(moderationAsyncService, times(2)).moderatePostsSublistAsync(anyList());
    }
}