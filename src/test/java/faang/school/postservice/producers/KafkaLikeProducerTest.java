package faang.school.postservice.producers;

import faang.school.postservice.dto.LikeEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class KafkaLikeProducerTest {

    @InjectMocks
    private KafkaLikesProducer kafkaLikesProducer;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;


    @Test
    void testsendEvent() {
        LikeEvent event = new LikeEvent(1L, 2L, 3L, LocalDateTime.now());
        RecordMetadata recordMetadata = new RecordMetadata(
                new TopicPartition("likes", 1),
                0L,
                0L,
                0L,
                0L,
                0,
                0
        );
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(
                new SendResult<>(new ProducerRecord<>("likes", event), recordMetadata)
        );
        when(kafkaTemplate.send(eq("likes"), eq(event)))
                .thenReturn(future);
        kafkaLikesProducer.sendEvent(event,"likes");
        verify(kafkaTemplate).send(eq("likes"), eq(event));
    }
}