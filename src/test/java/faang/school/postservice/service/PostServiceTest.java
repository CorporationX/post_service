package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.ModerationDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private ModerationDictionary moderationDictionary;
    private final Integer batchSize = 100;
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postService = new PostService(postRepository, moderationDictionary, batchSize);
    }

    @Test
    void verifyContent_Test() {
        List<Post> posts = new ArrayList<>();

        for (int i = 0; i < batchSize * 2; i++) {
            Post post = Post.builder()
                    .id(i)
                    .verified(false)
                    .verifiedAt(null)
                    .build();
            if (post.getId() % 2 == 0) {
                post.setContent("Something arse something");
            } else {
                post.setContent("To verify");
            }
            posts.add(post);
        }

        when(postRepository.findAllByVerifiedAtIsNull()).thenReturn(posts);
        when(moderationDictionary.containsBadWord(anyString())).thenReturn(false);

        postService.verifyContent();

        verify(postRepository).findAllByVerifiedAtIsNull();
        verify(moderationDictionary, times(posts.size())).containsBadWord(anyString());
        verify(postRepository, times(posts.size() / batchSize)).saveAll(anyList());
    }
}