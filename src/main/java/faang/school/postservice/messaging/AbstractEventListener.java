package faang.school.postservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.service.ParserHashtagsService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.message.MessageBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventListener {

    protected final ObjectMapper objectMapper;
    protected final PostService postService;
    protected final List<ParserHashtagsService> parserHashtagsServices;
    protected final List<MessageBuilder<?>> messageBuilders;

    protected String getMessage(Class<?> eventType, Locale locale, String... args) {
        return messageBuilders.stream()
                .filter(builder -> builder.getEvent() == eventType)
                .findFirst()
                .map(builder -> builder.getMessage(locale, args))
                .orElseThrow(() -> new IllegalArgumentException("No message builder found for event: " + eventType));
    }

    protected void addHashtags(long postId, String message) {
        var post = postService.getPostById(postId);
        parserHashtagsServices.stream()
                .filter(service -> service.getHashtags() == user.getPreference())
                .findFirst()
                .ifPresentOrElse(
                        service -> service.send(user, message),
                        () -> log.warn("No notification service found for user: {} and preference: {}", user, user.getPreference())
                );
    }
}
