package faang.school.postservice.service.post;

import faang.school.postservice.dictionary.PostModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.impl.verifier.PostContentVerifierImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class PostContentVerifierTest {

    @Mock
    private PostModerationDictionary postModerationDictionary;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostContentVerifierImpl postContentVerifier;

    private Set<String> banWords;
    private Post post;

    @BeforeEach
    public void setup() {
        banWords = Set.of("bad");
    }

    @Test
    public void testVerifyPostsSuccess() {
        when(postModerationDictionary.getForbiddenWords()).thenReturn(banWords);
        post = Post.builder()
                .id(1)
                .authorId(2L)
                .content("some text")
                .build();
        List<Post> posts = List.of(post);
        postContentVerifier.verifyPosts(posts);

        assertTrue(post.isVerified());
        assertNotNull(post.getVerifiedDate());
        verify(postRepository, times(1)).saveAll(posts);
    }

    @Test
    public void testVerifyPostsContainBadWords() {
        when(postModerationDictionary.getForbiddenWords()).thenReturn(banWords);
        post = Post.builder()
                .id(1)
                .authorId(2L)
                .content("bad")
                .build();
        List<Post> posts = List.of(post);
        postContentVerifier.verifyPosts(posts);

        assertFalse(post.isVerified());
        assertNotNull(post.getVerifiedDate());
        verify(postRepository, times(1)).saveAll(posts);
    }
}
