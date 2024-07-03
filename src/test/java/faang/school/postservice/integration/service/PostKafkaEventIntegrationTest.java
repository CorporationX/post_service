package faang.school.postservice.integration.service;

import faang.school.postservice.dto.event.PostKafkaEvent;
import faang.school.postservice.integration.IntegrationTestBase;
import faang.school.postservice.producer.KafkaPostProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@DirtiesContext
public class PostKafkaEventIntegrationTest extends IntegrationTestBase {

    @Autowired
    private KafkaPostProducer kafkaPostProducer;

    @Test
    public void shouldSendKafkaEvent() {
        PostKafkaEvent event = new PostKafkaEvent(1L, List.of(2L, 3L, 4L, 5L));
        kafkaPostProducer.sendEvent(event);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
