package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsyncPostPublishServiceTest {
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private AsyncPostPublishService asyncPostPublishService;

    @Test
    void publishPost() {
        //Arrange
        List<Post> posts = List.of(
                Post.builder().content("text").authorId(1L).published(false).build()
        );
        //Act
        asyncPostPublishService.publishPost(posts);
        //Assert
        verify(postRepository, times(1)).saveAll(posts);
    }
}