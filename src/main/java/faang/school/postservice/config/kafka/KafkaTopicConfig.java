package faang.school.postservice.config.kafka;

import faang.school.postservice.property.KafkaChannelProperty;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaChannelProperty kafkaChannelProperty;

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
}
