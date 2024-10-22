package faang.school.postservice.service.impl;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.ModerationDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModerationPostServiceImplTest {

    @InjectMocks
    private ModerationPostServiceImpl moderationPostService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModerationDictionary moderationDictionary;

    private final Post verifiedPost = new Post();
    private final Post unverifiedPost = new Post();

    @BeforeEach
    void setUp() {
        verifiedPost.setVerified(true);
        unverifiedPost.setVerified(false);
    }

    @Test
    void testModerationPosts_WithVerifiedPosts() {
        when(postRepository.findAll()).thenReturn(List.of(verifiedPost));
        when(moderationDictionary.isVerified(verifiedPost)).thenReturn(true);

        moderationPostService.moderationPosts();

        verify(postRepository).saveAll(any());
        verify(postRepository).findAll();
    }

    @Test
    void testModerationPosts_WithUnverifiedPosts() {
        when(postRepository.findAll()).thenReturn(List.of(unverifiedPost));
        when(moderationDictionary.isVerified(unverifiedPost)).thenReturn(false);

        moderationPostService.moderationPosts();

        verify(postRepository).saveAll(any());
        verify(postRepository).findAll();
    }

    @Test
    void testModerationPosts_ShouldGroupPostsCorrectly() {
        when(postRepository.findAll()).thenReturn(List.of(verifiedPost, unverifiedPost));
        when(moderationDictionary.isVerified(verifiedPost)).thenReturn(true);
        when(moderationDictionary.isVerified(unverifiedPost)).thenReturn(false);

        moderationPostService.moderationPosts();

        verify(postRepository).saveAll(any());
        verify(postRepository).findAll();
    }
}
