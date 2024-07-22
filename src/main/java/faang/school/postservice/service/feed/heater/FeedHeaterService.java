package faang.school.postservice.service.feed.heater;

import java.util.List;
import java.util.Map;

public interface FeedHeaterService {
    void heatUp();

    void handleBatch(Map<Long, List<Long>> userBatch);
}
