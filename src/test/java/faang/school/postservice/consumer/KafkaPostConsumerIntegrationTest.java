package faang.school.postservice.consumer;

import faang.school.postservice.service.redis.RedisFeedService;
import faang.school.postservice.util.BaseContextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

import static faang.school.postservice.constant.PostEventTestConstants.POST_AUTHOR_FOLLOWERS_IDS_LIST;
import static faang.school.postservice.constant.PostEventTestConstants.POST_EVENT_DTO;

class KafkaPostConsumerIntegrationTest extends BaseContextTest {

    @Autowired
    private KafkaPostConsumer kafkaPostConsumer;

    @Autowired
    private RedisFeedService redisFeedService;

    @Test
    void consume_shouldBeCompletedSuccessfully() {
        Long followerId = POST_AUTHOR_FOLLOWERS_IDS_LIST.get(0);
        Set<Long> expectedFollowerPosts = Set.of(POST_AUTHOR_FOLLOWERS_IDS_LIST.get(0));

        kafkaPostConsumer.consume(POST_EVENT_DTO, () -> {});

        Set<Long> actualFollowerPosts = redisFeedService.getPostsIdsListInReverseOrder(followerId).stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .collect(Collectors.toSet());

        Assertions.assertEquals(expectedFollowerPosts, actualFollowerPosts);
    }
}