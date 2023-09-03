package faang.school.postservice.service;

import faang.school.postservice.config.ModerationProperties;
import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.ContentModerator;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Assertions;
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

    private final Executor executor = Executors.newSingleThreadExecutor();
    private ModerationProperties moderationProperties = new ModerationProperties();

    private ContentModerator contentModerator;

    private List<Post> posts;

    @BeforeEach
    public void setUp() {
        moderationProperties.setSecondsBetweenModeration(3600);
        moderationProperties.setBatchSize(3);
        contentModerator = new ContentModerator(postService, moderationDictionary, executor, moderationProperties);

        posts = Stream.iterate(0, i -> i + 1).limit(3)
                .map(i -> Post.builder().id(i).verified(false).build()).toList();
        Mockito.when(postService.getAllPosts()).thenReturn(posts);

    }

    @Test
    void contentModerationTest() throws InterruptedException {
        var time = LocalDateTime.now().minusDays(1);
        posts.forEach(post -> post.setVerifiedDate(time));

        contentModerator.moderate();
        Thread.sleep(1000); // Асинхронное выполнение, нужно подождать

        Assertions.assertTrue(posts.stream().allMatch(Post::isVerified));
    }

    @Test
    void UpdatedAfterModerationTest() throws InterruptedException {
        var time = LocalDateTime.now();
        posts.forEach(post -> {
            post.setVerifiedDate(time.minusMinutes(30));
            post.setUpdatedAt(time);
        });
        contentModerator.moderate();

        Thread.sleep(1000); // Асинхронное выполнение, нужно подождать
        Assertions.assertTrue(posts.stream().allMatch(Post::isVerified));
    }
}
