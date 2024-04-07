package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModerationServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private ModerationDictionary moderationDictionary;
    @Mock
    private PostService postService;
    @InjectMocks
    private ModerationService moderationService;

    @Test
    void testCheckPostsWithBadWordSuccessful() {
        Post postTrue = Post.builder()
                .id(1)
                .content("true")
                .build();
        Post postFalse = Post.builder()
                .id(2)
                .content("false")
                .build();
        moderationService.setBatchSize(100);
        List<Post> posts = List.of(postTrue, postFalse);
        when(postRepository.findAllByVerifiedAtNull()).thenReturn(posts);
        when(moderationDictionary.containsBadWord("true")).thenReturn(false);
        when(moderationDictionary.containsBadWord("false")).thenReturn(true);

        postService.checkPostsWithBadWord();

        assertTrue(posts.get(0).isVerified());
        assertFalse(posts.get(1).isVerified());

        verify(postRepository).saveAll(posts);
    }
}