package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerifyStatus;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private PostService postService;

    Post post;

    @BeforeEach
    public void init() {
        post = Post.builder()
                .content("Content")
                .build();
    }

    @Test
    @DisplayName("Moderate posts when true")
    public void moderatePostsTrue() {
        when(postRepository.findNotVerifiedPosts()).thenReturn(List.of(post));
        when(moderationDictionary.checkString(post.getContent())).thenReturn(true);

        postService.moderateAll();

        assertEquals(VerifyStatus.VERIFIED, post.getVerifyStatus());
    }

    @Test
    @DisplayName("Moderate posts when false")
    public void moderatePostsFalse() {
        when(postRepository.findNotVerifiedPosts()).thenReturn(List.of(post));
        when(moderationDictionary.checkString(post.getContent())).thenReturn(false);

        postService.moderateAll();

        assertEquals(VerifyStatus.NOT_VERIFIED, post.getVerifyStatus());
    }
}