package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostPublishDto;
import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostEventPublisher;
import faang.school.postservice.service.post.PostKafkaEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostKafkaEventPublisherTest {
    private static final long VALID_POST_ID = 1L;
    private static final long VALID_AUTHOR_ID = 1L;
    private static final long VALID_FOLLOWER_ID = 2L;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostEventPublisher postEventPublisher;
    @InjectMocks
    private PostKafkaEventPublisher postKafkaEventPublisher;
    private Post post;

    @BeforeEach
    public void init() {
        post = new Post();
    }

    @Test
    public void givenPostWhenPutPostToKafkaThenExit() {
        post.setAuthorId(VALID_AUTHOR_ID);
        postKafkaEventPublisher.putPostToKafka(post);

        verify(postEventPublisher, times(0)).publish(any(PostPublishDto.class));
    }

    @Test
    public void givenPostWhenPutPostToKafkaThenEventPublished() {
        post.setId(VALID_POST_ID);
        post.setAuthorId(VALID_AUTHOR_ID);
        when(userServiceClient.getFollowerIds(VALID_AUTHOR_ID))
                .thenReturn(List.of(UserViewDto.builder().id(VALID_FOLLOWER_ID).build()));

        postKafkaEventPublisher.putPostToKafka(post);

        verify(postEventPublisher, times(1)).publish(any(PostPublishDto.class));

    }
}
