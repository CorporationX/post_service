package faang.school.postservice.properties;

import faang.school.postservice.model.event.EventType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kafka.producer")
public class KafkaProperties {
    private String bootstrapServers;
    private Map<String, String> topics;

    public String getTopic(@NonNull EventType eventType) {
        return topics.get(eventType.name());
    }
}
