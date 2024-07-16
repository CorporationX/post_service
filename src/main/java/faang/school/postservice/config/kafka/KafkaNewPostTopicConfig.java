package faang.school.postservice.config.kafka;

import faang.school.postservice.property.ChannelProperty;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class KafkaNewPostTopicConfig {

    @Value("${spring.data.kafka.default-partition}")
    private int defaultPartition;
    @Value("${spring.data.kafka.default-replication}")
    private short defaultReplication;
    private final ChannelProperty channelProperty;

    @Bean
    public Map<String, NewTopic> topicMap() {

        return channelProperty.getChannels().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            ChannelProperty.Channel channel = entry.getValue();

                            if (channel.getPartition() == null) {
                                channel.setPartition(defaultPartition);
                            }

                            if (channel.getReplication() == null) {
                                channel.setReplication(defaultReplication);
                            }

                            return new NewTopic(channel.getName(), channel.getPartition(), channel.getReplication());
                        })
                );
    }
}
