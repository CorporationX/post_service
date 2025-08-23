package faang.school.postservice.service;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.dto.kafka.KafkaPostEventDto;
import faang.school.postservice.dto.kafka.KafkaSubscribersFeedHeatDto;

import java.util.List;

public interface FeedService {

    void initializeFeedHeat();

    void gatherFollowersForUsers(List<Long> userIds);

    void fillFollowersFeed(KafkaSubscribersFeedHeatDto dto);

    void newPostCreated(KafkaPostEventDto eventDto);

    void putUserIntoCache(long userId);

    boolean putCommentInCache(KafkaCommentEventDto commentEventDto);

    boolean updatePost(long postId, String event);

    FeedDto getFeed(Long postId);
}
