package faang.school.postservice.model.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeatTaskEventKafka {
    private long startUserId;
    private long endUserId;
}
