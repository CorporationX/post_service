package faang.school.postservice.service;

import faang.school.postservice.dto.kafka.PostViewEvent;
import faang.school.postservice.kafka.KafkaPostViewProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaPostViewProducerTest {

    @Mock
    private KafkaTemplate<String, PostViewEvent> kafkaTemplate;

    @InjectMocks
    private KafkaPostViewProducer kafkaPostViewProducer;

    private final String topic = "post-views-topic";
    private PostViewEvent event;

    @BeforeEach
    void setUp() throws Exception {
        kafkaPostViewProducer = new KafkaPostViewProducer(kafkaTemplate);

        Field field = KafkaPostViewProducer.class.getDeclaredField("postViewsTopic");
        field.setAccessible(true);
        field.set(kafkaPostViewProducer, topic);

        event = PostViewEvent.builder()
                .postId(123L)
                .build();
    }

    @Test
    void testSendSuccess() throws ExecutionException, InterruptedException {
        CompletableFuture<SendResult<String, PostViewEvent>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(null, null));

        when(kafkaTemplate.send(topic, event)).thenReturn(future);

        kafkaPostViewProducer.send(event);

        verify(kafkaTemplate).send(topic, event);
        Thread.sleep(100);
    }

    @Test
    void testSendFailure() throws ExecutionException, InterruptedException {
        CompletableFuture<SendResult<String, PostViewEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(topic, event)).thenReturn(future);

        kafkaPostViewProducer.send(event);

        verify(kafkaTemplate).send(topic, event);
        Thread.sleep(100);
    }

    @Test
    void testSendWithDifferentPostId() throws ExecutionException, InterruptedException {
        PostViewEvent anotherEvent = PostViewEvent.builder()
                .postId(456L)
                .build();

        CompletableFuture<SendResult<String, PostViewEvent>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(null, null));

        when(kafkaTemplate.send(topic, anotherEvent)).thenReturn(future);

        kafkaPostViewProducer.send(anotherEvent);

        verify(kafkaTemplate).send(topic, anotherEvent);
        Thread.sleep(100);
    }
}