package faang.school.postservice.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikePostEvent;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikePostEventPublisherTest {

    @InjectMocks
    private LikePostEventPublisher likePostEventPublisher;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private ChannelTopic likePostTopic;

    private static final long ID = 1L;
    private static final String MESSAGE = "Message";
    private static final String EXCEPTION_MESSAGE = "Error";
    private static final String LOG_MESSAGE = "Failed to created message with event " +
            "- LikePostEvent(postAuthorId=1, likeAuthorId=1, postId=1). Error";

    private LikePostEvent event;
    private LogCaptor logCaptor;

    @BeforeEach
    public void init() {
        event = LikePostEvent.builder()
                .postId(ID)
                .postAuthorId(ID)
                .likeAuthorId(ID)
                .build();
        logCaptor = LogCaptor.forClass(LikePostEventPublisher.class);
    }

    @Test
    @DisplayName("Успешное создание message и его отправка")
    public void whenPublishEventShouldSuccess() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenReturn(MESSAGE);

        likePostEventPublisher.publishEvent(event);

        verify(objectMapper).writeValueAsString(event);
        verify(redisTemplate).convertAndSend(likePostTopic.getTopic(), MESSAGE);
    }

    @Test
    @DisplayName("Успешная запись информации об ошибке в log")
    public void whenPublishEventThrowException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event))
                .thenThrow(new JsonProcessingException(EXCEPTION_MESSAGE) {
        });

        likePostEventPublisher.publishEvent(event);

        assertThat(logCaptor.getErrorLogs()).contains(LOG_MESSAGE);
    }
}