package faang.school.postservice.service;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.ContentModerator;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class ContentModeratorTest {
    @Mock
    private PostService postService;

    @Mock
    private ModerationDictionary moderationDictionary;

    private Executor executor = Executors.newSingleThreadExecutor();

    @InjectMocks
    private ContentModerator contentModerator;

    private List<Post> posts;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        contentModerator = new ContentModerator(postService, moderationDictionary, executor);

        posts = Stream.iterate(0, i -> i + 1).limit(10)
                .map(i -> Post.builder().id(i).build()).toList();
        Mockito.when(postService.getAllPosts()).thenReturn(posts);

    }

    @Test
    void t() {
        var time = LocalDateTime.now();
        posts.forEach(post -> post.setVerifiedDate(time.minusDays(12)));
        System.out.println(1);
        contentModerator.moderate();
        System.out.println(2);
        posts.forEach(System.out::println);
    }

}
