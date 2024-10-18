package faang.school.postservice.kafka.event.feed_heater;

import faang.school.postservice.kafka.event.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class FeedHeaterEvent implements KafkaEvent {
    private Map<Long, List<Long>> usersAndFollowees;
}
