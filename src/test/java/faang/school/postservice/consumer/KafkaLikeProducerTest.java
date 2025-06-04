package faang.school.postservice.consumer;

import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.publisher.KafkaLikeProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(classes = {KafkaLikeProducer.class, TestConfig.class})
public class KafkaLikeProducerTest extends TestContainer {

    @Autowired
    private KafkaLikeProducer kafkaLikeProducer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "t.like";

    private static KafkaConsumer<String, String> testConsumer;

    @BeforeAll
    static void setUpConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        testConsumer = new KafkaConsumer<>(props);
        testConsumer.subscribe(Collections.singletonList(TOPIC));
    }

    @AfterAll
    static void tearDownConsumer() {
        testConsumer.close();
    }

    @Test
    void shouldProduceLikeEventToKafka() {
        LikePostEvent event = new LikePostEvent(42L, 1L, 99L);

        kafkaLikeProducer.sendMessage(event);

        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    ConsumerRecords<String, String> records = testConsumer.poll(Duration.ofMillis(100));
                    boolean messageReceived = false;
                    for (ConsumerRecord<String, String> record : records) {
                        if (record.value().contains("\"postId\":99")) {
                            messageReceived = true;
                            break;
                        }
                    }
                    assertTrue(messageReceived, "Expected message was not received from Kafka topic.");
                });
    }
}
