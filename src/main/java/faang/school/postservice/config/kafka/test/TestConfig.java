package faang.school.postservice.config.kafka.test;

import faang.school.postservice.service.producer.EventProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class TestConfig {
  @Bean
  public NewTopic testTopic() {
    return TopicBuilder.name("test_topic")
            .partitions(1)
            .replicas(1)
            .build();
  }

  @Bean
  public EventProducer<TestEvent> testProducer(KafkaTemplate<String, Object> template,
                                               NewTopic testTopic) {
    return new EventProducer<>(template, testTopic);
  }
}
