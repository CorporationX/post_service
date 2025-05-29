package faang.school.postservice.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
@Validated
public class KafkaProperties {

    @NotBlank
    private String bootstrapServers;

    @NotBlank
    private String groupId;
    private String topic = "connectivity_check";
    private Integer concurrency = 3;
    private Integer maxPoll;
    private Topics topics;

    @Getter
    @Setter
    public static class Topics {
        private String postCreated;
        private String likes;
        private String comments;
    }
}
