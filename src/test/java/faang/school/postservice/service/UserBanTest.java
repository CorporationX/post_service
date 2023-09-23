package faang.school.postservice.service;

import faang.school.postservice.messaging.userbanevent.UserBanEventPublisher;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserBanTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private UserBanEventPublisher userBanEventPublisher;

    @InjectMocks
    private PostService postService;


    @Test
    void userBan() {
        Mockito.when(postRepository.findAll())
                .thenReturn(List.of(
                        Post.builder()
                                .authorId(2L)
                                .verified(false)
                                .build(),
                        Post.builder()
                                .authorId(2L)
                                .verified(false)
                                .build(),
                        Post.builder()
                                .authorId(2L)
                                .verified(false)
                                .build(),
                        Post.builder()
                                .authorId(2L)
                                .verified(false)
                                .build(),
                        Post.builder()
                                .authorId(2L)
                                .verified(false)
                                .build(),
                        Post.builder()
                                .authorId(2L)
                                .verified(false)
                                .build()));

        postService.banUser();
        Mockito.verify(userBanEventPublisher, Mockito.times(1))
                .publish(2L);
    }
}
