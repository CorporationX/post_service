package faang.school.postservice.config.kafka;

import faang.school.postservice.property.KafkaChannelProperty;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaChannelProperty kafkaChannelProperty;

    @Value(value = "${spring.data.kafka.bootstrap_servers}")
    private String bootstrapAddress;

    @Bean
    public Map<String, NewTopic> topicMap() {

        return kafkaChannelProperty.getTopicSettings().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            KafkaChannelProperty.Channel channel = entry.getValue();

                            if (channel.getPartition() == null) {
                                channel.setPartition(kafkaChannelProperty.getDefaultPartition());
                            }

                            if (channel.getReplication() == null) {
                                channel.setReplication(kafkaChannelProperty.getDefaultReplication());
                            }

                            return new NewTopic(channel.getName(), channel.getPartition(), channel.getReplication());
                        })
                );
    }

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public KafkaAdmin.NewTopics adminTopics(Map<String, NewTopic> topicMap) {
        return new KafkaAdmin.NewTopics(topicMap.values().toArray(NewTopic[]::new));
    }
}
