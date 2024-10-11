package faang.school.postservice.kafka.event.heater;

import faang.school.postservice.cache.model.UserRedis;
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
public class HeaterUsersEvent extends Event {
    private List<UserRedis> users;
}
