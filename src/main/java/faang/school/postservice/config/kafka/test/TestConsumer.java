package faang.school.postservice.config.kafka.test;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@NoArgsConstructor
@Slf4j
public class TestConsumer {

  @KafkaListener(topics = "test_topic")
  public void listenerTestEvent(TestEvent event, Acknowledgment acknowledgment) {

    try {
      System.out.println("listenerTestEvent: " + event);

      acknowledgment.acknowledge();

    } catch (Exception e) {
      log.error("Error processing event: {}", event, e);
      acknowledgment.nack(Duration.ofSeconds(5));
    }
  }
}
