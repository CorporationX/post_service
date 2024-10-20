package faang.school.postservice.moderation;

import faang.school.postservice.model.ModerationStatus;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.scheduler.post.moderation.AhoCorasickContentChecker;
import faang.school.postservice.service.ContentModerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContentModerationServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private AhoCorasickContentChecker contentChecker;

    @InjectMocks
    private ContentModerationService contentModerationService;

    private List<Post> posts;

    @BeforeEach
    public void setUp() {
        Post post1 = new Post();
        post1.setContent("This is a clean post.");
        post1.setModerationStatus(ModerationStatus.UNVERIFIED);

        Post post2 = new Post();
        post2.setContent("This post contains badword.");
        post2.setModerationStatus(ModerationStatus.UNVERIFIED);

        posts = Arrays.asList(post1, post2);
    }

    @Test
    public void testCheckContentAndModerate_VerifiedPost() {
        when(contentChecker.containsBadContent("This is a clean post.")).thenReturn(false);

        contentModerationService.checkContentAndModerate(posts);

        assertEquals(ModerationStatus.VERIFIED, posts.get(0).getModerationStatus());
    }

    @Test
    public void testCheckContentAndModerate_RejectedPost() {
        when(contentChecker.containsBadContent(anyString())).thenReturn(true);

        contentModerationService.checkContentAndModerate(posts);

        assertEquals(ModerationStatus.REJECTED, posts.get(1).getModerationStatus());
    }

    @Test
    public void testCheckContentAndModerate_SaveAllPosts() {
        when(contentChecker.containsBadContent(anyString())).thenReturn(false);

        contentModerationService.checkContentAndModerate(posts);

        verify(postRepository, times(1)).saveAll(posts);
    }
}



