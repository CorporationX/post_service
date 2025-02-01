package faang.school.postservice.async;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.post.KafkaPostProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AsyncPostEventSenderTest {
    @Mock
    private KafkaPostProducer kafkaPostProducer;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private AsyncPostEventSender asyncPostEventSender;

    @Test
    void testEmptyPostId() {
        Post post = new Post();

        asyncPostEventSender.sendPostEvents(post);
        Mockito.verify(userServiceClient, times(0)).getFollowerIds(anyLong());
        Mockito.verify(kafkaPostProducer, times(0)).sendMessage(any());
    }

    @Test
    void testSendPostEvent() {
        Mockito.when(userServiceClient.getFollowerIds(anyLong())).thenReturn(List.of(1L, 2L));

        Post post = Post.builder()
                .id(1L)
                .authorId(1L)
                .build();

        asyncPostEventSender.sendPostEvents(post);
        Mockito.verify(userServiceClient, times(1)).getFollowerIds(anyLong());
        Mockito.verify(kafkaPostProducer, times(1)).sendMessage(any());
    }
}
