package faang.school.postservice.consumer;

import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.service.redis.RedisFeedService;
import faang.school.postservice.util.BaseContextTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class KafkaPostConsumerIntegrationTest extends BaseContextTest {

    private static final List<Long> POST_AUTHOR_FOLLOWERS_IDS_LIST = List.of(2L, 3L, 4L, 5L, 6L, 7L, 8L);

    private static final long POST_ID = 2L;
    private static final LocalDateTime CURRENT_LOCAL_DATE_TIME = LocalDateTime.now();

    private static final PostEventDto POST_EVENT_DTO
            = new PostEventDto(POST_ID, CURRENT_LOCAL_DATE_TIME, POST_AUTHOR_FOLLOWERS_IDS_LIST);

    @Autowired
    private KafkaPostConsumer kafkaPostConsumer;

    @Autowired
    private RedisFeedService redisFeedService;

    @Test
    void consumeSuccessfully() {
        Long followerId = POST_AUTHOR_FOLLOWERS_IDS_LIST.get(0);
        Set<Long> expectedFollowerPosts = Set.of(POST_AUTHOR_FOLLOWERS_IDS_LIST.get(0));

        kafkaPostConsumer.consume(POST_EVENT_DTO, () -> {});

        Set<Long> actualFollowerPosts = redisFeedService.getPostsIdsListInReverseOrder(followerId).stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .collect(Collectors.toSet());

        Assertions.assertThat(actualFollowerPosts).isEqualTo(expectedFollowerPosts);
    }
}