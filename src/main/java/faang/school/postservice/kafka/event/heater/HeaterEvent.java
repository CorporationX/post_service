package faang.school.postservice.kafka.event.heater;

import faang.school.postservice.kafka.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class HeaterEvent extends Event {
    private List<Long> followerIds;
}
