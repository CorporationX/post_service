package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.post.PostDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.kafka.topic.producer.post-views=post-views"
})
class KafkaPublisherServiceTest {

    @Autowired
    private KafkaPublisherService kafkaPublisherService;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void publishingPostToKafka_Success() {
        PostDto postDto = PostDto.builder()
                .id(1L)
                .content("Sample content")
                .authorId(1L)
                .build();

        kafkaPublisherService.publishingPostToKafka(postDto);

        verify(kafkaTemplate, times(1)).send("post-views", postDto);
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void publishingPostToKafka_LogsCorrectMessage() {
        PostDto postDto = PostDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(123L)
                .build();

        kafkaPublisherService.publishingPostToKafka(postDto);

        verify(kafkaTemplate, times(1)).send("post-views", postDto);
    }
}