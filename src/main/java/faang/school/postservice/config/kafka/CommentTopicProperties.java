package faang.school.postservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "kafka.topic")
@Getter
@Setter
public class CommentTopicProperties {
    private List<String> commentEvents;
}