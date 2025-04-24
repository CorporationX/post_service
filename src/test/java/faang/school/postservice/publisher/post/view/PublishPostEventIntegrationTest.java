package faang.school.postservice.publisher.post.view;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.Event;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.event.PostViewEventBuffer;
import faang.school.postservice.config.context.UserContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PublishPostEventIntegrationTest {

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private UserContext userContext;

    @Autowired
    private PostViewEventBuffer postViewEventBuffer;

    @Autowired
    private PostService postService;

    private Post createValidPost(Long id, Long authorId) {
        Post post = new Post();
        post.setId(id);
        post.setAuthorId(authorId);
        post.setPublished(true);
        post.setDeleted(false);
        post.setPublishedAt(LocalDateTime.now());
        return post;
    }

    @Test
    void testGetPost_PublishesEvents() {
        Post post = createValidPost(1L, 2L);
        Long viewerId = 3L;

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userContext.getUserId()).thenReturn(viewerId);

        postService.get(1L);

        List<Event> events = postViewEventBuffer.flush();
        assertEquals(2, events.size(), "There must be 2 events published");
    }

    @Test
    void testGetPostsByAuthorId_PublishesAnalyticsEvents() {
        Long authorId = 100L;
        Long viewerId = 200L;
        Post post1 = createValidPost(1L, authorId);
        Post post2 = createValidPost(2L, authorId);

        when(postRepository.findByAuthorId(anyLong())).thenReturn(List.of(post1, post2));
        when(userContext.getUserId()).thenReturn(viewerId);

        postService.getPostsByAuthorId(authorId);

        List<Event> events = postViewEventBuffer.flush();
        assertEquals(2, events.size(), "There must be 2 analytical events published");
    }
}