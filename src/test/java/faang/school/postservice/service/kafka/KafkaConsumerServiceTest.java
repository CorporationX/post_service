package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.KafkaMessageException;
import faang.school.postservice.service.post.PostService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private PostService postService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;


    @Test
    void listenPaymentRequest_Success() {
        PostDto postDto = PostDto.builder()
                .id(1L)
                .content("Sample content")
                .authorId(1L)
                .createdAt(LocalDateTime.now())
                .build();
        ConsumerRecord<String, PostDto> record = new ConsumerRecord<>("test-topic", 0, 0L, "key", postDto);

        kafkaConsumerService.listenPaymentRequest(record, acknowledgment);

        verify(postService, times(1)).savePostInDataBase(postDto);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void listenPaymentRequest_ExceptionThrown() {
        PostDto postDto = PostDto.builder()
                .id(1L)
                .content("Sample content")
                .authorId(1L)
                .createdAt(LocalDateTime.now())
                .build();
        ConsumerRecord<String, PostDto> record = new ConsumerRecord<>("test-topic", 0, 0L, "key", postDto);
        doThrow(new RuntimeException("Database error")).when(postService).savePostInDataBase(any(PostDto.class));

        assertThrows(KafkaMessageException.class, () -> kafkaConsumerService.listenPaymentRequest(record, acknowledgment));

        verify(postService, times(1)).savePostInDataBase(postDto);
    }
}