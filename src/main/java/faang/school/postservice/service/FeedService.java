package faang.school.postservice.service;

import faang.school.postservice.dto.feed.UserFeedHeatDto;
import faang.school.postservice.dto.kafka.KafkaPostEventDto;

import java.util.List;

public interface FeedService {

    void initializeFeedHeat();
    void fillCacheForUsers(List<UserFeedHeatDto> userIds);
    void addCreatedPostToSubscribers(KafkaPostEventDto eventDto);
}
