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

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static faang.school.postservice.constant.PostEventTestConstants.LOCAL_DATE_TIME_NOW;
import static faang.school.postservice.constant.PostEventTestConstants.POST_AUTHOR_FOLLOWERS_IDS_LIST;
import static faang.school.postservice.constant.PostEventTestConstants.POST_AUTHOR_ID;
import static faang.school.postservice.constant.PostEventTestConstants.POST_EVENT_DTO;
import static faang.school.postservice.constant.PostEventTestConstants.POST_ID;

@EmbeddedKafka(
        topics = "${spring.kafka.topic.posts.name}",
        brokerProperties = {"listeners=PLAINTEXT://${spring.kafka.bootstrap-servers}", "port=9092"})
class KafkaPostProducerIntegrationTest extends BaseContextTest {

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
    void produce_shouldBeCompletedSuccessfully() {
        Mockito.when(userServiceClient.getFollowersIds(POST_AUTHOR_ID)).thenReturn(POST_AUTHOR_FOLLOWERS_IDS_LIST);

        try (Consumer<String, PostEventDto> consumer = postEventConsumerFactory.createConsumer()) {
            consumer.subscribe(Collections.singleton(postsTopicProperties.name()));
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, postsTopicProperties.name());

            kafkaPostProducer.produce(POST_ID, LOCAL_DATE_TIME_NOW, POST_AUTHOR_ID);

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