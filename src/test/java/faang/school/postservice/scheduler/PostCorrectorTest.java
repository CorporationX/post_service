package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostCorrectorTest {
    private final static int BATCH_SIZE = 1000;

    @Mock
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostCorrecter postCorrecter;

    private List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        Field batchSizeField = PostCorrecter.class.getDeclaredField("batchSize");
        batchSizeField.setAccessible(true);
        batchSizeField.set(postCorrecter, BATCH_SIZE);

        posts.add(Post.builder()
                .id(1L)
                .content("Content 1")
                .deleted(false)
                .published(false)
                .authorId(1L)
                .createdAt(LocalDateTime.of(2024, 9, 17, 0, 0))
                .publishedAt(LocalDateTime.of(2024, 9, 17, 0, 0))
                .build());

        posts.add(Post.builder()
                .id(2L)
                .content("Content 2")
                .deleted(false)
                .published(false)
                .authorId(2L)
                .createdAt(LocalDateTime.of(2024, 9, 16, 0, 0))
                .publishedAt(LocalDateTime.of(2024, 9, 16, 0, 0))
                .build());
    }

    @Test
    void testStartCheckAISpellingPostsCreate() {
        Pageable pageable = PageRequest.of(0, BATCH_SIZE, Sort.by("id").ascending());

        when(postRepository.findDraftsPaginate(pageable)).thenReturn(posts);
        when(postRepository.findDraftsPaginate(
                PageRequest.of(1, BATCH_SIZE, Sort.by("id").ascending()
                ))).thenReturn(new ArrayList<>());

        postCorrecter.startCheckAISpellingPosts();

        verify(postRepository, times(2)).findDraftsPaginate(any());
        verify(postService, times(2)).correctPosts(any());
    }
}
