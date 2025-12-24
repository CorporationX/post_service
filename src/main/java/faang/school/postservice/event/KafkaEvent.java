package faang.school.postservice.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        property = "@class"
)
public interface KafkaEvent {
}
