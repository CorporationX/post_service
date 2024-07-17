package faang.school.postservice.config.kafka;

import faang.school.postservice.property.ChannelProperty;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final ChannelProperty channelProperty;

    @Bean
    public Map<String, NewTopic> topicMap() {

        return channelProperty.getTopicSettings().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            ChannelProperty.Channel channel = entry.getValue();

                            if (channel.getPartition() == null) {
                                channel.setPartition(channelProperty.getDefaultPartition());
                            }

                            if (channel.getReplication() == null) {
                                channel.setReplication(channelProperty.getDefaultReplication());
                            }

                            return new NewTopic(channel.getName(), channel.getPartition(), channel.getReplication());
                        })
                );
    }
}
