package faang.school.postservice.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class PublicationService<T extends MessagePublisher, E> {
    private final T publisher;
    private final ObjectMapper objectMapper;

    public void publishEvent(E event){
        publisher.publish(toJson(event));
    }

    private String toJson(E object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e){
            throw new RuntimeException("Не возможно сериализовать.");
        }
    }
}
