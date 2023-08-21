//package faang.school.postservice.publisher;
//
//import faang.school.postservice.config.context.UserContext;
//import faang.school.postservice.dto.post.PostViewEvent;
//import faang.school.postservice.model.Post;
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//import org.springframework.kafka.test.EmbeddedKafkaBroker;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.kafka.test.utils.KafkaTestUtils;
//import org.springframework.test.annotation.DirtiesContext;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.Map;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@DirtiesContext
//@EmbeddedKafka(
//        partitions = 1,
//        topics = {"post-view"},
//        brokerProperties = {
//                "listeners=PLAINTEXT://localhost:9092",
//                "port=9092"
//        })
//class PostViewEventPublisherTest {
//
//    @Autowired
//    private EmbeddedKafkaBroker embeddedKafkaBroker;
//
//    @Autowired
//    private PostViewEventPublisher postViewEventPublisher;
//
//    @MockBean
//    private UserContext userContext;
//
//    private final String TOPIC = "post-view";
//
//    @BeforeEach
//    void setUp() {
//        Mockito.lenient().when(userContext.getUserId()).thenReturn(1L);
//    }
//
//    @Test
//    void publish_ShouldBeAddedToTopic() {
//        postViewEventPublisher.publish(buildPost());
//
//        Consumer<String, PostViewEvent> consumer = createConsumer(TOPIC);
//
//        ConsumerRecord<String, PostViewEvent> record =
//                KafkaTestUtils.getSingleRecord(consumer, TOPIC);
//
//        PostViewEvent messageReceived = record.value();
//
//        Assertions.assertEquals(1L, messageReceived.getPostId());
//    }
//
//    private Consumer<String, PostViewEvent> createConsumer(String topicName) {
//        Map<String, Object> consumerProps =
//                KafkaTestUtils.consumerProps("group_consumer_test", "false", embeddedKafkaBroker);
//
//        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//
//        Consumer<String, PostViewEvent> consumer =
//                new DefaultKafkaConsumerFactory<>(consumerProps,
//                        new StringDeserializer(),
//                        new JsonDeserializer<>(PostViewEvent.class, false)).createConsumer();
//
//        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, topicName);
//
//        return consumer;
//    }
//
//    private Post buildPost() {
//        return Post.builder()
//                .id(1L)
//                .content("content")
//                .authorId(1L)
//                .likes(new ArrayList<>())
//                .comments(new ArrayList<>())
//                .albums(new ArrayList<>())
//                .published(false)
//                .deleted(false)
//                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
//                .build();
//    }
//}
