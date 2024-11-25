package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HeatFeedCacheEvent {
    private List<Long> usersIds;
}
