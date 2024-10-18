package faang.school.postservice.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.service.publisher.messagePublishers.CommentEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {
    @Mock
    private CommentEventPublisher commentEventPublisher;
    @Spy
    private ObjectMapper objectMapper;
    @Captor
    private ArgumentCaptor<String> jsonCaptor;
    @InjectMocks
    private PublicationService<CommentEventPublisher, CommentEvent> publicationService;

    @Test
    void testPublishCommentEvent() throws JsonProcessingException {
        // given
        CommentEvent event = CommentEvent.builder()
                .id(1L)
                .authorId(2L)
                .postId(3L)
                .content("content")
                .build();
        String jsonEvent = objectMapper.writeValueAsString(event);
        // when
        publicationService.publishEvent(event);
        //then
        verify(commentEventPublisher, times(1)).publish(jsonCaptor.capture());
        assertEquals(jsonEvent, jsonCaptor.getValue());
    }
}