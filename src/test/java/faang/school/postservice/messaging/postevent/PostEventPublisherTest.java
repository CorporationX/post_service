package faang.school.postservice.messaging.postevent;

import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        topics = {"post-publication"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        })
class PostEventPublisherTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private PostEventPublisher postEventPublisher;

    private final String TOPIC = "post-publication";

    @Test
    @Disabled
    void send_ShouldSendToBroker() {
        Consumer<String, PostEvent> consumerServiceTest = createConsumer(TOPIC);

        postEventPublisher.send(mockPost());

        ConsumerRecord<String, PostEvent> record =
                KafkaTestUtils.getSingleRecord(consumerServiceTest, TOPIC);

        PostEvent eventReceived = record.value();

        Assertions.assertEquals(mockPostEvent(), eventReceived);
    }

    private Consumer<String, PostEvent> createConsumer(final String topicName) {
        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps("group_consumer_test", "false", embeddedKafkaBroker);

        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<String, PostEvent> consumer =
                new DefaultKafkaConsumerFactory<>(consumerProps,
                        new StringDeserializer(),
                        new JsonDeserializer<>(PostEvent.class, false))
                        .createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, topicName);

        return consumer;
    }

    private Post mockPost() {
        return Post.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .albums(new ArrayList<>())
                .published(false)
                .deleted(false)
                .publishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

    private PostEvent mockPostEvent() {
        return PostEvent.builder()
                .postId(1L)
                .content("content")
                .userAuthorId(1L)
                .projectAuthorId(null)
                .publishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }
}
