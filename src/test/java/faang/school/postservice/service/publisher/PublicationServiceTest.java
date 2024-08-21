package faang.school.postservice.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {
    @InjectMocks
    private PublicationService publicationService;
    @Mock
    private CommentEventPublisher commentEventPublisher;
    @Spy
    private ObjectMapper objectMapper;

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
        publicationService.publishCommentEvent(event);
        //then
        verify(commentEventPublisher, times(1)).publish(jsonEvent);
    }
}