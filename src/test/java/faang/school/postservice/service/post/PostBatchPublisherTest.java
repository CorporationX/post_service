package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostBatchPublisherTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostBatchPublisher postBatchPublisher;

    @Test
    void testPublishPostsWhenThereArePostsToPublish() {
        List<Post> postsToPublish = LongStream.range(1, 3)
                .mapToObj(id -> Post.builder().id(id).published(false).build())
                .toList();

        postBatchPublisher.publishPosts(postsToPublish);

        ArgumentCaptor<List<Post>> captor = ArgumentCaptor.forClass(List.class);
        verify(postRepository).saveAll(captor.capture());

        List<Post> savedPosts = captor.getValue();
        assertEquals(2, savedPosts.size());

        savedPosts.forEach(post -> {
            assertTrue(post.isPublished(), "Пост должен быть отмечен как опубликованный");
            assertNotNull(post.getPublishedAt(), "Дата публикации должна быть установлена");
        });
    }
}