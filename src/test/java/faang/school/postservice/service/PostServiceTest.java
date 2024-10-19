package faang.school.postservice.service;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private PostService postService;

    private Set<String> banWords;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(postService, "countPostsInThread", 10);
        banWords = Set.of("badword1", "badword2");
       //
    }

    @Test
    public void testModeratePosts_WithPosts() {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            posts.add(Post.builder().content("Valid Content" + i).build());
        }

        when(postRepository.findNotVerified()).thenReturn(posts);

        postService.moderatePosts();

        verify(postRepository, times(1)).findNotVerified();
        verify(moderationDictionary, times(3)).getBadWords();
    }

    @Test
    public void testModeratePosts_NoPosts() {
        when(postRepository.findNotVerified()).thenReturn(new ArrayList<>());

        postService.moderatePosts();

        verify(postRepository, times(1)).findNotVerified();
    }

    @Test
    public void testVerifyPosts_NoBanWords() {
        Post post = new Post();
        post.setContent("This is a clean post");

        postService.verifyPosts(List.of(post));

        assertTrue(post.isVerified());
        assertNotNull(post.getVerifiedDate());
    }

    @Test
    public void testVerifyPosts_WithBanWords() {
        Post post = new Post();
        post.setId(1L);
        post.setContent("This post contains badword1");
        when(moderationDictionary.getBadWords()).thenReturn(banWords);
        postService.verifyPosts(List.of(post));

        assertFalse(post.isVerified());
        assertNull(post.getVerifiedDate());
    }
}