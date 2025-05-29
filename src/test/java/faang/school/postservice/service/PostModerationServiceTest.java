package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostModerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostModerationServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostModerationService postModerationService;

    private Post cleanPost;
    private Post profanityPost;
    private final Set<String> profanityWords = Set.of("bad", "word");

    @BeforeEach
    void setUp() {
        cleanPost = new Post();
        cleanPost.setId(1L);
        cleanPost.setContent("This is a clean content");

        profanityPost = new Post();
        profanityPost.setId(2L);
        profanityPost.setContent("This contains bad word");
    }

    @Test
    @DisplayName("Пост без мата должен быть верифицирован")
    public void givenCleanPost_whenModerate_thenPostIsVerified() {
        postModerationService.moderatePosts(List.of(cleanPost), profanityWords);

        assertTrue(cleanPost.isVerified());
        assertNotNull(cleanPost.getVerifiedAt());
        verify(postRepository).saveAll(List.of(cleanPost));
    }

    @Test
    @DisplayName("Пост с матом не должен быть верифицирован")
    public void givenProfanityPost_whenModerate_thenPostIsNotVerified() {
        postModerationService.moderatePosts(List.of(profanityPost), profanityWords);

        assertFalse(profanityPost.isVerified());
        assertNotNull(profanityPost.getVerifiedAt());
        verify(postRepository).saveAll(List.of(profanityPost));
    }
}