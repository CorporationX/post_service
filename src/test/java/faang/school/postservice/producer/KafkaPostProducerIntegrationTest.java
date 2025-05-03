package faang.school.postservice.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.properties.PostsTopicProperties;
import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.util.BaseContextTest;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EmbeddedKafka(
        topics = "${spring.kafka.topic.posts.name}",
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class KafkaPostProducerIntegrationTest extends BaseContextTest {

    private static final long POST_AUTHOR_ID = 1L;
    private static final List<Long> POST_AUTHOR_FOLLOWERS_IDS_LIST = List.of(2L, 3L, 4L, 5L, 6L, 7L, 8L);

    private static final long POST_ID = 2L;
    private static final LocalDateTime CURRENT_LOCAL_DATE_TIME = LocalDateTime.now();

    private static final PostEventDto POST_EVENT_DTO
            = new PostEventDto(POST_ID, CURRENT_LOCAL_DATE_TIME, POST_AUTHOR_FOLLOWERS_IDS_LIST);

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ConsumerFactory<String, PostEventDto> postEventConsumerFactory;

    @Autowired
    private PostsTopicProperties postsTopicProperties;

    @Autowired
    private KafkaPostProducer kafkaPostProducer;

    @MockBean
    private UserServiceClient userServiceClient;

    @Test
    void produceSuccessfully() {
        Mockito.when(userServiceClient.getFollowersIds(POST_AUTHOR_ID)).thenReturn(POST_AUTHOR_FOLLOWERS_IDS_LIST);

        try (Consumer<String, PostEventDto> consumer = postEventConsumerFactory.createConsumer()) {
            consumer.subscribe(Collections.singleton(postsTopicProperties.name()));
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, postsTopicProperties.name());

            kafkaPostProducer.produce(POST_ID, CURRENT_LOCAL_DATE_TIME, POST_AUTHOR_ID);

            Awaitility.await().atMost(20, TimeUnit.SECONDS)
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .untilAsserted(() -> {
                        ConsumerRecords<String, PostEventDto> records = KafkaTestUtils.getRecords(consumer);

                        Assertions.assertThat(records).isNotEmpty();
                        Assertions.assertThat(
                                records.records(postsTopicProperties.name()).iterator().next().value()
                        ).isEqualTo(POST_EVENT_DTO);
                    });
        }
    }
}