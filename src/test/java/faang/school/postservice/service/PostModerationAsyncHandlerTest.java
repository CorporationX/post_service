package faang.school.postservice.service;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostModerationAsyncHandler;
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
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostModerationAsyncHandlerTest {
    @Mock
    private PostModerationService postModerationService;

    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private PostModerationAsyncHandler postModerationAsyncHandler;

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
    @DisplayName("Проверка поста, пост должен быть верифицирован")
    public void givenCleanPost_whenCheckForProfanity_thenPostVerified() {
        when(moderationDictionary.getProfanityWords()).thenReturn(profanityWords);
        doNothing().when(postModerationService).moderatePosts(any(), any());

        CompletableFuture<Void> result = postModerationAsyncHandler.checkForProfanity(List.of(cleanPost));

        assertNull(result.join());
    }

    @Test
    @DisplayName("Проверка поста, пост не должен быть верифицирован")
    public void givenProfanityPost_whenCheckForProfanity_thenPostNotVerified() {
        when(moderationDictionary.getProfanityWords()).thenReturn(profanityWords);
        doNothing().when(postModerationService).moderatePosts(any(), any());

        CompletableFuture<Void> result = postModerationAsyncHandler.checkForProfanity(List.of(profanityPost));

        assertNull(result.join());
    }
}