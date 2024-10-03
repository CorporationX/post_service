package faang.school.postservice.service.post;

import faang.school.postservice.config.TestAsyncConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerificationPostStatus;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@Import(TestAsyncConfig.class)
public class ModerationPostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private ModerationPostService moderationPostService;

    private static final int SUBLIST_SIZE = 10;
    private static final String CLEAN_POST_CONTENT = "This is a clean post";
    private static final String FORBIDDEN_POST_CONTENT = "This post contains a forbidden word";

    private Post post1;
    private Post post2;
    private List<Post> posts;

    @BeforeEach
    public void setUp() throws Exception {
        Field field = ModerationPostService.class.getDeclaredField("sublistSize");
        field.setAccessible(true);
        field.set(moderationPostService, SUBLIST_SIZE);

        post1 = new Post();
        post1.setContent(CLEAN_POST_CONTENT);
        post1.setVerificationStatus(VerificationPostStatus.UNVERIFIED);

        post2 = new Post();
        post2.setContent(FORBIDDEN_POST_CONTENT);
        post2.setVerificationStatus(VerificationPostStatus.UNVERIFIED);

        posts = Arrays.asList(post1, post2);

        when(moderationDictionary.containsForbiddenWord(CLEAN_POST_CONTENT)).thenReturn(false);
        when(moderationDictionary.containsForbiddenWord(FORBIDDEN_POST_CONTENT)).thenReturn(true);
    }

    @Test
    public void testModeratePostsSublist() {
        moderationPostService.moderatePostsSublist(posts);

        assertEquals(VerificationPostStatus.VERIFIED, post1.getVerificationStatus());
        assertEquals(VerificationPostStatus.REJECTED, post2.getVerificationStatus());
        assertNotNull(post1.getVerifiedDate());
        assertNotNull(post2.getVerifiedDate());

        verify(postRepository, times(1)).saveAll(posts);
    }

    @Test
    public void testModerateUnverifiedPosts() throws InterruptedException {
        when(postRepository.findUnverifiedPosts()).thenReturn(posts);

        moderationPostService.moderateUnverifiedPosts();

        Thread.sleep(1000);

        assertEquals(VerificationPostStatus.VERIFIED, post1.getVerificationStatus());
        assertEquals(VerificationPostStatus.REJECTED, post2.getVerificationStatus());
        assertNotNull(post1.getVerifiedDate());
        assertNotNull(post2.getVerifiedDate());

        ArgumentCaptor<List<Post>> captor = ArgumentCaptor.forClass(List.class);
        verify(postRepository, times(1)).saveAll(captor.capture());

        List<Post> savedPosts = captor.getValue();

        assertTrue(savedPosts.containsAll(posts));
    }
}