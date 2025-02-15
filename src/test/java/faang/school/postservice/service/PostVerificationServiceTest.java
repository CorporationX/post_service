package faang.school.postservice.service;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class    PostVerificationServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    PostVerificationService postVerificationService;

    @Test
    public void testCheckAndVerifyPostsInBatch() {
        Post post1 = Post.builder()
                .content("Clean post")
                .verifiedDate(LocalDateTime.now())
                .build();

        Post post2 = Post.builder()
                .content("Bad post with forbidden word")
                .verifiedDate(LocalDateTime.now())
                .build();

        List<Post> postsToVerify = Arrays.asList(post1, post2);

        when(moderationDictionary.containsForbiddenWord("Clean post")).thenReturn(false);
        when(moderationDictionary.containsForbiddenWord("Bad post with forbidden word")).thenReturn(true);

        CompletableFuture<Void> future = postVerificationService.checkAndVerifyPostsInBatch(postsToVerify);

        future.join();

        verify(postRepository, times(2)).save(any(Post.class));

        assertTrue(post1.getVerified());
        assertFalse(post2.getVerified());
    }
}
