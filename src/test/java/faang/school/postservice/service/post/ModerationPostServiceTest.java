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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModerationPostServiceTest {
    private static final String CLEAN_CONTENT = "This is a clean post.";
    private static final String BAD_CONTENT = "This contains badword1.";
    private static final VerificationPostStatus UNVERIFIED_STATUS = VerificationPostStatus.UNVERIFIED;
    private static final VerificationPostStatus VERIFIED_STATUS = VerificationPostStatus.VERIFIED;
    private static final VerificationPostStatus REJECTED_STATUS = VerificationPostStatus.REJECTED;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private ModerationPostService moderationPostService;

    private Post cleanPost;
    private Post badPost;

    @BeforeEach
    void setUp() {
        cleanPost = new Post();
        cleanPost.setContent(CLEAN_CONTENT);

        badPost = new Post();
        badPost.setContent(BAD_CONTENT);
    }

    @Test
    void testModeratePostsSublist_VerifyModeration() {
        List<Post> posts = Arrays.asList(cleanPost, badPost);

        when(moderationDictionary.containsForbiddenWord(CLEAN_CONTENT)).thenReturn(false);
        when(moderationDictionary.containsForbiddenWord(BAD_CONTENT)).thenReturn(true);

        moderationPostService.moderatePostsSublist(posts);

        assertEquals(VERIFIED_STATUS, cleanPost.getVerificationStatus());
        assertEquals(REJECTED_STATUS, badPost.getVerificationStatus());
        assertNotNull(cleanPost.getVerifiedDate());
        assertNotNull(badPost.getVerifiedDate());

        verify(postRepository, times(1)).saveAll(posts);
    }

    @Test
    void testFindUnverifiedPosts() {
        Post post = new Post();
        post.setVerificationStatus(UNVERIFIED_STATUS);

        List<Post> unverifiedPosts = Collections.singletonList(post);

        when(postRepository.findByVerificationStatus(UNVERIFIED_STATUS)).thenReturn(unverifiedPosts);

        List<Post> result = moderationPostService.findUnverifiedPosts();

        assertEquals(1, result.size());
        assertEquals(UNVERIFIED_STATUS, result.get(0).getVerificationStatus());
    }

    @Test
    void testSplitListIntoSublists_ValidSublistSize() {
        Post post1 = new Post();
        Post post2 = new Post();
        Post post3 = new Post();
        List<Post> posts = Arrays.asList(post1, post2, post3);

        List<List<Post>> sublists = moderationPostService.splitListIntoSublists(posts, 2);

        assertEquals(2, sublists.size());
        assertEquals(2, sublists.get(0).size());
        assertEquals(1, sublists.get(1).size());
    }
}