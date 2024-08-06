package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SpellCheckerService spellCheckerService;

    private List<Post> postList;

    @BeforeEach
    public void setUp() {
        long firstPostId = 1L;
        long secondPostId = 2L;
        String firstPostContent = "FirstPostContent";
        String secondPostContent = "SecondPostContent";

        postList = List.of(
                Post.builder()
                        .id(firstPostId)
                        .content(firstPostContent).build(),
                Post.builder()
                        .id(secondPostId)
                        .content(secondPostContent).build()
        );
    }

    @Test
    @DisplayName("testing correctPostsContent method")
    void testCorrectPostsContent() {
        postService.correctPostsContent(postList);
        verify(spellCheckerService, times(2)).checkMessage(anyString());
        verify(postRepository, times(1)).saveAll(postList);
    }
}