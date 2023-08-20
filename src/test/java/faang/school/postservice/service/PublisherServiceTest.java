package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.mapper.PostViewEventMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.redis.PostViewEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PublisherServiceTest {

    @Spy
    private PostViewEventMapperImpl postViewEventMapper;
    @Mock
    private PostViewEventPublisher eventPublisher;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private PublisherService publisherService;
    private Post post;

    @BeforeEach
    void initPost() {
        post = Post.builder()
                .id(1L)
                .authorId(2L)
                .build();
    }

    @Test
    void testPublishPostViewEventToRedis() {
        publisherService.publishPostViewEventToRedis(post);
        verify(userContext).getUserId();
        verify(eventPublisher).publish(any());
    }
}
