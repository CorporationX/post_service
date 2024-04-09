package faang.school.postservice.service;

import faang.school.postservice.dto.event.UserEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PublisherUsersBan;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PublisherUsersBan publisherUsersBan;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostService postService;

    @Test
    public void testBanUsersWithMultipleUnverifiedPostSuccessful(){
        List<Post> posts = getUnverifiedPosts();

        when(postRepository.findByVerified(false)).thenReturn(posts);

        Assertions.assertDoesNotThrow(() -> postService.banUsersWithMultipleUnverifiedPosts());
        verify(postRepository).findByVerified(false);
        verify(publisherUsersBan).publish(new UserEvent(1L));
    }

    private List<Post> getUnverifiedPosts(){
        return new ArrayList<>(List.of(
                Post.builder().verified(false).authorId(1L).build(),
                Post.builder().verified(true).authorId(2L).build()));
    }

}
