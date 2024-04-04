package faang.school.postservice.service.hash;

import faang.school.postservice.dto.event.PostEventKafka;

public interface FeedHashService {
    void updateFeed(PostEventKafka postEventKafka);

}
