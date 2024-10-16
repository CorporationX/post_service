package faang.school.postservice.service;

import faang.school.postservice.model.entity.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.PostgreSQLContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PostServiceIntegrationTest extends PostgreSQLContainerConfig {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Test
    public void testPublishScheduledPosts_OneButch() {
        postRepository.deleteAll();

        List<Long> ids = Arrays.asList(1L, 2L);
        List<Post> posts = createPosts(ids);
        postRepository.saveAll(posts);

        List<CompletableFuture<Void>> futures = postService.publishScheduledPosts();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<Post> publishedPosts = postRepository.findAll();
        assertEquals(2, publishedPosts.size());

        publishedPosts.forEach(post -> {
            assertTrue(post.isPublished(), "Post should be published");
            assertNotNull(post.getPublishedAt(), "Post should have a publishedAt timestamp");
            assertTrue(post.getPublishedAt().isBefore(LocalDateTime.now()), "publishedAt should be set in the past");
        });
    }

    @Test
    public void testPublishScheduledPosts_ManyButch() {
        postRepository.deleteAll();

        List<Long> ids = Arrays.asList(11L, 22L, 33L, 44L);
        List<Post> posts = createPosts(ids);
        postRepository.saveAll(posts);

        List<CompletableFuture<Void>> futures = postService.publishScheduledPosts();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<Post> publishedPosts = postRepository.findAll();
        assertEquals(4, publishedPosts.size());

        publishedPosts.forEach(post -> {
            assertTrue(post.isPublished(), "Post should be published");
            assertNotNull(post.getPublishedAt(), "Post should have a publishedAt timestamp");
            assertTrue(post.getPublishedAt().isBefore(LocalDateTime.now()), "publishedAt should be set in the past");
        });
    }

    private List<Post> createPosts(List<Long> ids) {
        return ids.stream().map(id -> {
            Post post = new Post();
            post.setId(id);
            post.setContent("Test post " + id);
            post.setPublished(false);
            post.setDeleted(false);
            post.setScheduledAt(LocalDateTime.now().minusMinutes(1));
            return post;
        }).collect(Collectors.toList());
    }
}