package faang.school.postservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.service.publisher.messagePublisherImpl.CommentEventPublisher;
import faang.school.postservice.service.publisher.PublicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
public class ConfPublicationService {

    @Bean
    public PublicationService<CommentEventPublisher, CommentEvent> publicationService(CommentEventPublisher commentEventPublisher,
                                                                                      ObjectMapper objectMapper) {
        return new PublicationService<>(commentEventPublisher, objectMapper);
    }
}