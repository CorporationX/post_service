package faang.school.postservice.producer.kafka;

import faang.school.postservice.event.PostEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PostKafkaProducerTest {
    @Autowired
    PostKafkaProducer postKafkaProducer;

    @Test
    public void testSendEvent() {
        postKafkaProducer.send(
                PostEvent.builder()
                        .postId(1L)
                        .authorId(1L)
                        .build()
        );
    }
}
