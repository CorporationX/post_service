package faang.school.postservice.service.post.async;

import faang.school.postservice.model.entity.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.impl.post.async.PostServiceAsyncImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PostServiceAsyncImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModerationDictionary dictionary;

    @InjectMocks
    private PostServiceAsyncImpl postServiceAsync;

    @Test
    void testModeratePostsByBatches() {
        Post post = Post.builder().content("abu").verified(null).verifiedDate(null).build();
        Post post2 = Post.builder().content("bandit").verified(null).verifiedDate(null).build();
        List<Post> posts = List.of(post, post2);

        when(dictionary.containsBadWords(posts.get(1).getContent())).thenReturn(true);
        when(dictionary.containsBadWords(posts.get(0).getContent())).thenReturn(false);

        postServiceAsync.moderatePostsByBatches(posts);

        verify(dictionary, times(2)).containsBadWords(anyString());
        verify(postRepository).saveAll(any());

        assertTrue(posts.get(0).getVerified());
        assertFalse(posts.get(1).getVerified());
    }
}
