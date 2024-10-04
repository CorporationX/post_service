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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModerationProcessingServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private ModerationProcessingService moderationProcessingService;

    private Post postOne;
    private Post postTwo;

    @BeforeEach
    void setUp() {
        postOne = Post.builder()
                .content("This is a badword post.")
                .verificationStatus(VerificationPostStatus.UNVERIFIED)
                .build();

        postTwo = Post.builder()
                .content("This is a clean post.")
                .verificationStatus(VerificationPostStatus.UNVERIFIED)
                .build();
    }

    @Test
    void testModeratePostsSublist() {
        List<Post> posts = Arrays.asList(postOne, postTwo);

        when(moderationDictionary.containsForbiddenWord("This is a badword post.")).thenReturn(true);
        when(moderationDictionary.containsForbiddenWord("This is a clean post.")).thenReturn(false);

        moderationProcessingService.moderatePostsSublist(posts);

        assertEquals(VerificationPostStatus.REJECTED, postOne.getVerificationStatus());
        assertEquals(VerificationPostStatus.VERIFIED, postTwo.getVerificationStatus());
        assertNotNull(postOne.getVerifiedDate());
        assertNotNull(postTwo.getVerifiedDate());
        verify(postRepository, times(1)).saveAll(posts);
    }
}