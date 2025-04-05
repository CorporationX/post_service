package faang.school.postservice.service.feed;

import java.util.List;

public interface FeedHeaterService {

    void heatFeed();

    void heatFeedByUsersList(List<Long> userIds);
}
